package aar92_22.library.Interfaces;

public interface CategoryListener {

    void renameCategoryListener(int id, String newName);

    void addNewCategoryListener(String name);

    void deleteCategoryListener(int id);
}
