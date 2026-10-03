package com.chinmai.splitwise.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="group_membership",
uniqueConstraints = {
@UniqueConstraint(columnNames = {"group_id", "user_id"})
})
    

public class GroupMembership {

    @Id //primary key
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long membershipId;

    @ManyToOne 
    @JoinColumn(name = "group_id") //foreign key
    private Group group;
    

    @ManyToOne 
    @JoinColumn(name = "user_id") //foreign key
    private User user;
 
    private LocalDateTime joinedAt;

    public GroupMembership() {
    }

    public Long getMembershipId() {
        return membershipId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    @PrePersist 
    protected void onCreated() {
    joinedAt= LocalDateTime.now();
    }  
}
