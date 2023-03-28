/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.entities.tag;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getTagById(Integer id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Tag %d not found!", id)));
    }

    @Override
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Tag %s not found!", name)));
    }

    @Override
    public Tag createNewTag(Tag tag) {
        checkIfNameIsAlreadyInUse(tag);

        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTagById(Integer id, Tag tag) {

        tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Tag %d not found!", id)));

        checkIfNameIsAlreadyInUse(tag);

        tag.setId(id);

        return tagRepository.save(tag);
    }

    @Override
    public Tag patchTagById(Integer id, Tag tag) {
        return tagRepository.findById(id).map(savedEntity -> {

            if (tag.getName() != null) {
                checkIfNameIsAlreadyInUse(tag);

                savedEntity.setName(tag.getName());
            }

            if (tag.getColor() != null)
                savedEntity.setColor(tag.getColor());

            if (tag.getTransactions() != null && tag.getTransactions().size() != 0)
                savedEntity.setTransactions(tag.getTransactions());

            return tagRepository.save(savedEntity);

        }).orElseThrow(() -> new ResourceNotFoundException(String.format("Tag %d not found!", id)));
    }

    @Override
    public void deleteTagById(Integer id) {
        tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Tag %d not found!", id)));

        tagRepository.deleteById(id);
    }

    @Override
    public Set<Transaction> getTagTransactionsById(Integer id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Tag %d not found!", id)));

        return tag.getTransactions();
    }


    private void checkIfNameIsAlreadyInUse(Tag Tag) {
        tagRepository.findByName(Tag.getName()).ifPresent(tag -> {
            throw new ResourceAlreadyExistsException(String.format("Tag %s already exists", Tag.getName()),
                    "/api/tags/" + tag.getId());
        });
    }
}
