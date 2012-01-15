package model.manager.excel.interfaces;

public interface ImportValidator<T> {

	String validate(T value);

}
