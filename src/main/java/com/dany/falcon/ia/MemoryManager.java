package com.dany.falcon.ia;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.dany.falcon.database.Database;

public class MemoryManager {
    private final Database db;
    private List<MemoryItem> memories = new ArrayList<>();
    private int maxMemories = 50;

    public MemoryManager(List<MemoryItem> memories) {
        this.memories = memories;
        this.db = Database.getInstance();
    }

    public void addMemory(MemoryItem memory) {
        if (memories.size() >= maxMemories) {
            memories.removeFirst(); // elimina la más antigua
        }
        System.out.println(memory.getContent() + memory.getType().toString());
        memories.add(memory);
        System.out.println("todas las memorias: ");
        System.out.println(getMemoriesAsPrompt());
        saveMemories(memory);
    }

    public List<MemoryItem> getValidMemories() {
        return memories.stream()
                .filter(m -> !m.isExpired())
                .collect(Collectors.toList());
    }

    public String getMemoriesAsPrompt() {
        return getValidMemories().stream()
                .map(MemoryItem::getContent)
                .collect(Collectors.joining("\n"));
    }
    public void saveMemories (MemoryItem mem){
        this.db.saveOrUpdateMemory(mem);
    }

    public void setMemories(List<MemoryItem> memories) {
        this.memories = memories;
    }
}