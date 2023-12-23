package org.nurma.aqyndar.repository;

import org.nurma.aqyndar.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    @Transactional
    default List<Topic> insertIfNotExist(List<String> topicNames) {
        List<String> uniqueTopicNames = topicNames.stream().distinct().toList();
        List<Topic> topics = new ArrayList<>();
        for (String name : uniqueTopicNames) {
            Topic topic = findByName(name);
            if (topic == null) {
                topic = new Topic();
                topic.setName(name);
                topic = save(topic);
            }
            topics.add(topic);
        }
        return topics;
    }

    Topic findByName(String name);
}
