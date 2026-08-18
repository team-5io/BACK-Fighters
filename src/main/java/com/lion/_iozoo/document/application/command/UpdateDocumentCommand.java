package com.lion._iozoo.document.application.command;

import com.lion._iozoo.document.domain.Block;

import java.util.List;

public record UpdateDocumentCommand(
        String title,
        List<Block> blocks
) {
}
