package com.springhealth.intervention.domain;

import java.util.ArrayList;
import java.util.Collection;

public class UserList {
    private Collection<User> data = new ArrayList<>(); // 或者使用 List<User>

    public Collection<User> getData() {
        return data;
    }

    // 如果需要，可以添加 setter 方法
    public void setData(Collection<User> data) {
        this.data = data;
    }
}
