package com.lostfound.controller;
 
import com.lostfound.entity.Item.ItemStatus;
import com.lostfound.entity.Item.ItemType;
import com.lostfound.repository.ItemRepository;
import com.lostfound.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import java.util.HashMap;
import java.util.Map;
 
@RestController
@RequestMapping("/api/stats")
public class StatsController {
 
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
 
    public StatsController(ItemRepository itemRepository,
                           UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }
 
    @GetMapping
    public ResponseEntity<Map<String, Long>> getPublicStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalItems",    itemRepository.count());
        stats.put("lostItems",     itemRepository.countByType(ItemType.LOST));
        stats.put("foundItems",    itemRepository.countByType(ItemType.FOUND));
        stats.put("resolvedItems", itemRepository.countByStatus(ItemStatus.RESOLVED));
        stats.put("totalUsers",    userRepository.count());
        return ResponseEntity.ok(stats);
    }
}