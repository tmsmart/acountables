package group.g203.acountables.path.main.view;


import group.g203.acountables.base.view.BaseView;

public interface MainView extends BaseView {

    void displayEmptyView();

    void displayLoading();

    void finishLoading();

    void displayCountables();

    void addCountable();

    void deleteCountable();

    void undoCountableDelete();

    void displayInfoDialog();

    void displaySortDialog();

    void displayCreditsDialog();

    void enableAddButton();

    void disableAddButton();

    void resetAddCountableView();
}