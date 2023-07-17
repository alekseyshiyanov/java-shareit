package ru.practicum.shareit.item;

import lombok.*;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity
@Indexed(index = "idx_item")
@Table(name = "item", schema = "public")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@AnalyzerDef(name = "textAnalyzer",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = { @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                    @TokenFilterDef(
                        factory = StopFilterFactory.class,
                        params = {
                                @Parameter(name = "words", value = "stopwords_ru.txt"),
                                @Parameter(name = "ignoreCase", value = "true")
                        }
                )
        })
@Analyzer(definition = "textAnalyzer")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Field(name = "nameFiltered", termVector = TermVector.YES)
    @Column(name = "name", nullable = false)
    private String name;

    @Field(name = "descriptionFiltered", termVector = TermVector.YES)
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "owner")
    private User user;

    @Column(name = "request")
    private Integer request;
}
