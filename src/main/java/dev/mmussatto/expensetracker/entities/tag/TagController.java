/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.entities.tag;

import dev.mmussatto.expensetracker.entities.helpers.PageDTO;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.transaction.TransactionDTO;
import dev.mmussatto.expensetracker.entities.transaction.TransactionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "CRUD API for Tag entity")
@Validated
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    public TagController(TagService tagService, TagMapper tagMapper) {
        this.tagService = tagService;
        this.tagMapper = tagMapper;
    }



    @Operation(summary = "Get all tags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the tags", useReturnTypeSchema = true)
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TagDTO> getAllTags () {

        return tagService.getAllTags()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Operation(summary = "Get a tag by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the tag", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO getTagById (@PathVariable final Integer id) {
        return convertToDTO(tagService.getTagById(id));
    }


    @Operation(summary = "Get a tag by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the tag", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid name supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content)
    })
    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO getTagByName (@PathVariable final String name) {
        return convertToDTO(tagService.getTagByName(name));
    }


    @Operation(summary = "Create tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tag created", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name supplied is already in use", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(TagDTO.allFieldsValidation.class)
    public TagDTO createNewTag (@Valid @RequestBody TagDTO tagDTO) {
        Tag entity = convertToEntity(tagDTO);
        return convertToDTO(tagService.createNewTag(entity));
    }


    @Operation(summary = "Update tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag updated", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name supplied is already in use", content = @Content)
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(TagDTO.allFieldsValidation.class)
    public TagDTO updateTagById (@PathVariable final Integer id, @Valid @RequestBody TagDTO tagDTO) {
        Tag entity = convertToEntity(tagDTO);
        return convertToDTO(tagService.updateTagById(id, entity));
    }


    @Operation(summary = "Patch tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag patched", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name supplied is already in use", content = @Content)
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(TagDTO.onlyIdValidation.class)
    public TagDTO patchTagById (@PathVariable final Integer id, @Valid @RequestBody TagDTO tagDTO) {
        Tag entity = convertToEntity(tagDTO);
        return convertToDTO(tagService.patchTagById(id, entity));
    }


    @Operation(summary = "Delete tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tag deleted", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTagById (@PathVariable final Integer id) {
        tagService.deleteTagById(id);
    }


    //Transactions
    @Operation(summary = "Get all transactions from a tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the tag's transactions", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content)
    })
    @GetMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public PageDTO<TransactionDTO> getTransactionsByTagId(@PathVariable final Integer id,
                                                          @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                          @RequestParam(value = "size", defaultValue = "1", required = false) int size) {

        Page<Transaction> paginatedTransactions = tagService.getTransactionsByTagId(id, page, size);

        PageDTO<TransactionDTO> returnPage = new PageDTO<>();

        returnPage.setContent(paginatedTransactions.getContent()
                .stream()
                .map(transaction -> {
                    TransactionDTO dto = TransactionMapper.INSTANCE.convertToDTO(transaction);
                    dto.setPath("/api/transactions/" + dto.getId());
                    return dto;
                })
                .collect(Collectors.toList()));

        returnPage.setPageNo(paginatedTransactions.getNumber());
        returnPage.setPageSize(paginatedTransactions.getSize());
        returnPage.setTotalElements(paginatedTransactions.getTotalElements());
        returnPage.setTotalPages(paginatedTransactions.getTotalPages());

        if (paginatedTransactions.hasNext())
            returnPage.setNextPage(String.format("/api/tags/%d/transactions?page=%d&size=%d",
                    id, paginatedTransactions.getNumber()+1, paginatedTransactions.getSize()));

        if (paginatedTransactions.hasPrevious())
            returnPage.setPreviousPage(String.format("/api/tags/%d/transactions?page=%d&size=%d",
                    id, paginatedTransactions.getNumber()-1, paginatedTransactions.getSize()));

        return returnPage;
    }


    //Mappers
    private TagDTO convertToDTO (Tag entity) {
        TagDTO tagDTO = tagMapper.convertToDTO(entity);
        tagDTO.setPath("/api/tags/" + tagDTO.getId());
        return tagDTO;
    }

    private Tag convertToEntity (TagDTO dto) {
        return tagMapper.convertToEntity(dto);
    }
}
