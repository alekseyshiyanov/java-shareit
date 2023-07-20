package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Optional<ItemRequest> getItemRequestById(Long itemRequestId);

    List<ItemRequest> getItemRequestByRequesterId(Long requesterId);

    Boolean existsByRequesterId(Long userId);
}
