package hse.java.lectures.lecture3.practice.randomSet;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;


import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.fail;


import static org.junit.jupiter.api.Assertions.*;

class RandomSetBaseTest {

    @Test
    void try_find_desync_by_random_tries() {
        final int TRIES = 2000;       // количество попыток с разными seed'ами
        final int OPS = 2000;         // операций в одной попытке
        final int MAX_VAL = 500;      // диапазон значений

        for (int seed = 0; seed < TRIES; seed++) {
            Random rnd = new Random(seed);
            RandomSet<Integer> set = new RandomSet<>();
            Set<Integer> ref = new HashSet<>();

            try {
                for (int step = 0; step < OPS; step++) {
                    int v = rnd.nextInt(MAX_VAL);
                    boolean doInsert = rnd.nextInt(3) != 0; // чаще вставки

                    if (doInsert) {
                        boolean a = ref.add(v);
                        boolean b = set.insert(v);
                        if (a != b) {
                            throw new IllegalStateException("insert mismatch at seed=" + seed + " step=" + step + " value=" + v);
                        }
                    } else {
                        boolean a = ref.remove(v);
                        boolean b = set.remove(v);
                        if (a != b) {
                            throw new IllegalStateException("remove mismatch at seed=" + seed + " step=" + step + " value=" + v);
                        }
                    }

                    // периодически проверяем contains для всех элементов эталона
                    if (step % 200 == 0) {
                        for (int x : ref) {
                            if (!set.contains(x)) {
                                // вероятно возникла рассинхронизация: падаем и печатаем seed/step
                                fail("Contains mismatch -> possible desync. seed=" + seed + " step=" + step + " missing=" + x);
                            }
                        }
                    }
                }
            } catch (NullPointerException npe) {
                // Нуль-указатель — признак той самой проблемы; сообщаем seed и шаг
                fail("NullPointerException detected at seed=" + seed + " — reproduce by re-running with this seed. Exception: " + npe);
            } catch (AssertionError | IllegalStateException e) {
                // Любая другая несогласованность — тоже повод остановиться
                fail("Invariant violation at seed=" + seed + ": " + e.getMessage());
            }
            // иначе — эта попытка прошла без проблем, пробуем следующий seed
        }

        // Если дошли сюда — за TRIES попыток не поймали рассинхронизацию
    }

    @Test
    void reflection_forces_NPE_in_remove() throws Exception {
        RandomSet<Integer> set = new RandomSet<>();

        // вставляем несколько элементов
        set.insert(1); // index 0
        set.insert(2); // index 1
        set.insert(3); // index 2  <- последний элемент (id_end)

        // убедимся, что целевой элемент (value=1) есть
        if (!set.contains(1)) {
            fail("precondition failed: target not present");
        }

        // достаём private fields через reflection
        Field arrField = RandomSet.class.getDeclaredField("randomArray");
        Field sizeField = RandomSet.class.getDeclaredField("size");
        arrField.setAccessible(true);
        sizeField.setAccessible(true);

        Object[] arr = (Object[]) arrField.get(set);
        int size = sizeField.getInt(set);
        int idEnd = size - 1;

        // Инвариант нарушаем: заменяем последний элемент в массиве
        // на значение, которого нет в дереве. Это моделирует рассинхронизацию.
        arr[idEnd] = Integer.valueOf(9999999);
        arrField.set(set, arr);

        // Теперь вызов remove(1) в текущей реализации должен попытаться
        // найти movedNode по значению 9999999 в дереве и получить null,
        // а затем при обращении к end_node.index упадёт NPE.
        try {
            set.remove(1);
            fail("Expected remove(...) to throw NullPointerException (but it didn't)");
        } catch (NullPointerException npe) {
            // OK — это доказало уязвимость
            System.out.println("Got NPE as expected: " + npe);
        }
    }
}

