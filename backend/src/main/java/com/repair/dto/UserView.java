package com.repair.dto;

import com.repair.entity.User;
import lombok.Data;

@Data
public class UserView {
    private Long id;
    private String account;
    private Integer role;
    private String building;
    private String roomNo;

    public static UserView from(User user) {
        if (user == null) {
            return null;
        }
        UserView v = new UserView();
        v.setId(user.getId());
        v.setAccount(user.getAccount());
        v.setRole(user.getRole());
        v.setBuilding(user.getBuilding());
        v.setRoomNo(user.getRoomNo());
        return v;
    }
}
