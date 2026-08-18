package com.lion._iozoo.document.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Block {
    private final String id;
    private final String type;
    private final String content;
    private final Boolean checked;
    private final Boolean collapsed;
    private final String language;
    private final List<Block> children;

    @Builder
    @JsonCreator
    private Block(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type,
            @JsonProperty("content") String content,
            @JsonProperty("checked") Boolean checked,
            @JsonProperty("collapsed") Boolean collapsed,
            @JsonProperty("language") String language,
            @JsonProperty("children") List<Block> children) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.checked = checked;
        this.collapsed = collapsed;
        this.language = language;
        this.children = children == null ? List.of() : children;
    }
}
