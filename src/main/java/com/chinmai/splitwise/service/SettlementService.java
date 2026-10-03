package com.chinmai.splitwise.service;

import com.chinmai.splitwise.dto.SettlementRequest;
import com.chinmai.splitwise.entity.Settlement;
import com.chinmai.splitwise.repository.GroupMembershipRepository;
import com.chinmai.splitwise.repository.GroupRepository;
import com.chinmai.splitwise.repository.SettlementRepository;
import com.chinmai.splitwise.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository membershipRepository;

    public SettlementService(
            SettlementRepository settlementRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupMembershipRepository membershipRepository) {
        this.settlementRepository = settlementRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public Settlement createSettlement(SettlementRequest request) {

        var group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        var payer = userRepository.findById(request.getPaidByUserId())
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        var receiver = userRepository.findById(request.getPaidToUserId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (payer.getId().equals(receiver.getId())) {
            throw new RuntimeException("Users must be different");
        }

        if (!membershipRepository.existsByGroup_GroupIdAndUser_Id(
                group.getGroupId(), payer.getId())
                || !membershipRepository.existsByGroup_GroupIdAndUser_Id(
                group.getGroupId(), receiver.getId())) {
            throw new RuntimeException("Both users must belong to the group");
        }

        Settlement settlement = new Settlement();
        settlement.setGroup(group);
        settlement.setPaidBy(payer);
        settlement.setPaidTo(receiver);
        settlement.setAmount(request.getAmount());

        return settlementRepository.save(settlement);
    }
}