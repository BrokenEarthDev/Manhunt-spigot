package org.github.brokenearthdev.manhunt.gui.buttons;

/**
 * A simple binder
 *
 * @param <T> The type
 */
public class Binder<T> {

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
