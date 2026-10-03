package com.chinmai.splitwise.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.chinmai.splitwise.entity.GroupMembership;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
    boolean existsByGroup_GroupIdAndUser_Id(Long groupId, Long userId);
    List<GroupMembership> findAllByGroup_GroupId(Long groupId);
}
