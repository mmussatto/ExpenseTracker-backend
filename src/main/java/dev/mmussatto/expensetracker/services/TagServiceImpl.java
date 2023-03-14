/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.TagMapper;
import dev.mmussatto.expensetracker.api.model.TagDTO;
import dev.mmussatto.expensetracker.domain.Tag;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.repositories.TagRepository;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final TagRepository tagRepository;

    public TagServiceImpl(TagMapper tagMapper, TagRepository tagRepository) {
        this.tagMapper = tagMapper;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagDTO> getAllTags() {
        return tagRepository.findAll()
                .stream()
                .map(tag -> {
                    TagDTO tagDTO = tagMapper.convertToDTO(tag);
                    tagDTO.setPath("/api/tags/" + tagDTO.getId());
                    return tagDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TagDTO getTagById(Integer id) {
        return tagRepository.findById(id)
                .map(tag -> {
                    TagDTO tagDTO = tagMapper.convertToDTO(tag);
                    tagDTO.setPath("/api/tags/" + tagDTO.getId());
                    return tagDTO;
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Tag %d not found!", id)));
    }

    @Override
    public TagDTO getTagByName(String name) {
        return tagRepository.findByName(name)
                .map(tag -> {
                    TagDTO tagDTO = tagMapper.convertToDTO(tag);
                    tagDTO.setPath("/api/tags/" + tagDTO.getId());
                    return tagDTO;
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Tag %s not found!", name)));
    }

    @Override
    public TagDTO createNewTag(TagDTO tagDTO) {
        checkIfNameIsAlreadyInUse(tagDTO);

        return saveAndReturn(tagMapper.convertToEntity(tagDTO));
    }

    @Override
    public TagDTO updateTagById(Integer id, TagDTO tagDTO) {

        tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Tag %d not found!", id)));

        checkIfNameIsAlreadyInUse(tagDTO);

        Tag tag = tagMapper.convertToEntity(tagDTO);
        tag.setId(id);

        return saveAndReturn(tag);
    }

    @Override
    public TagDTO patchTagById(Integer id, TagDTO tagDTO) {
        return tagRepository.findById(id).map(tag -> {

            if (tagDTO.getName() != null) {
                checkIfNameIsAlreadyInUse(tagDTO);

                tag.setName(tagDTO.getName());
            }

            if (tagDTO.getColor() != null)
                tag.setColor(tagDTO.getColor());

            if (tagDTO.getTransactions() != null && tag.getTransactions().size() != 0)
                tag.setTransactions(tagDTO.getTransactions());

            return saveAndReturn(tag);

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



    private TagDTO saveAndReturn (Tag tag) {
        Tag savedTag = tagRepository.save(tag);

        TagDTO returnDTO = tagMapper.convertToDTO(savedTag);
        returnDTO.setPath("/api/tags/"+ returnDTO.getId());

        return returnDTO;
    }

    private void checkIfNameIsAlreadyInUse(TagDTO tagDTO) {
        tagRepository.findByName(tagDTO.getName()).ifPresent(tag -> {
            throw new ResourceAlreadyExistsException(String.format("Tag %s already exists", tagDTO.getName()),
                    "/api/tags/" + tag.getId());
        });
    }
}
