package com.blog.application.tags;

import com.blog.adapter.tags.repository.TagRepository;
import com.blog.application.tags.exceptions.TagNotFoundException;
import com.blog.domain.tag.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public List<Tag> findById(List<Long> tags) {
        List<Tag> allTagById = tagRepository.findAllTagById(tags);
        if (allTagById.size() == 0){
            throw new TagNotFoundException("Tag");
        }
        return allTagById;

    }
}
