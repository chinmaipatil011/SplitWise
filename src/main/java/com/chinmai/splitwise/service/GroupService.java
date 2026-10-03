package com.chinmai.splitwise.service;

import com.chinmai.splitwise.dto.GroupRequest;
import com.chinmai.splitwise.dto.GroupResponse;
import com.chinmai.splitwise.entity.Group;
import com.chinmai.splitwise.entity.GroupMembership;
import com.chinmai.splitwise.entity.User;
import com.chinmai.splitwise.repository.GroupMembershipRepository;
import com.chinmai.splitwise.repository.GroupRepository;
import com.chinmai.splitwise.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public GroupService(
            GroupRepository groupRepository,
            GroupMembershipRepository membershipRepository,
            UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public GroupResponse createGroup(GroupRequest request, Long creatorUserId) {

        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = new Group();
        group.setName(request.getName());
        group.setCreatedBy(creator);

        Group savedGroup = groupRepository.save(group);

        // Automatically make the creator a member
        GroupMembership membership = new GroupMembership();
        membership.setGroup(savedGroup);
        membership.setUser(creator);

        membershipRepository.save(membership);

        return new GroupResponse(
                savedGroup.getGroupId(),
                savedGroup.getName(),
                creator.getName()
        );
    }

    @Transactional
    public void addMember(Long groupId, Long userId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyMember =
                membershipRepository.existsByGroup_GroupIdAndUser_Id(groupId, userId);

        if (alreadyMember) {
            throw new RuntimeException("User is already a member of this group");
        }

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(user);

        membershipRepository.save(membership);
    }
}
