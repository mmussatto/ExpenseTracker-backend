/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.entities.tag;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;

import java.util.List;
import java.util.Set;

public interface TagService {

    List<Tag> getAllTags ();

    Tag getTagById (Integer id);

    Tag getTagByName (String name);

    Tag createNewTag (Tag tag);

    Tag updateTagById (Integer id, Tag tag);

    Tag patchTagById (Integer id, Tag tag);

    void deleteTagById (Integer id);

    Set<Transaction> getTagTransactionsById(Integer id);
}
