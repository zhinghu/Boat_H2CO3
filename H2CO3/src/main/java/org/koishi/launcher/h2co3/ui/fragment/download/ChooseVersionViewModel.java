package org.koishi.launcher.h2co3.ui.fragment.download;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChooseVersionViewModel extends ViewModel {
    private List<ChooseVersionFragment.Version> versionList = new ArrayList<>();
    private List<ChooseVersionFragment.Version> filteredList = new ArrayList<>();

    public List<ChooseVersionFragment.Version> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<ChooseVersionFragment.Version> versionList) {
        this.versionList = versionList;
    }

    public List<ChooseVersionFragment.Version> getFilteredList() {
        return filteredList;
    }

    public void setFilteredList(List<ChooseVersionFragment.Version> filteredList) {
        this.filteredList = filteredList;
    }
}