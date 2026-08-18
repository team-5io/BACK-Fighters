package com.lion._iozoo.document.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentTest {

    private Document document(Long authorId, boolean restricted, DocumentStatus status) {
        return Document.builder()
                .id(100L).teamId(1L).authorId(authorId)
                .title("제목").content("내용")
                .status(status).restricted(restricted)
                .build();
    }

    @Test
    void restricted가_아니면_역할_무관하게_FULL이다() {
        Document document = document(1L, false, DocumentStatus.DRAFT);

        assertThat(document.resolveAccessLevel(99L, null)).isEqualTo(DocumentAccessLevel.FULL);
        assertThat(document.resolveAccessLevel(99L, RaciRole.I)).isEqualTo(DocumentAccessLevel.FULL);
    }

    @Test
    void restricted여도_작성자는_FULL이다() {
        Document document = document(1L, true, DocumentStatus.DRAFT);

        assertThat(document.resolveAccessLevel(1L, null)).isEqualTo(DocumentAccessLevel.FULL);
    }

    @Test
    void restricted에서_R_A_C는_FULL이다() {
        Document document = document(1L, true, DocumentStatus.DRAFT);

        assertThat(document.resolveAccessLevel(10L, RaciRole.R)).isEqualTo(DocumentAccessLevel.FULL);
        assertThat(document.resolveAccessLevel(20L, RaciRole.A)).isEqualTo(DocumentAccessLevel.FULL);
        assertThat(document.resolveAccessLevel(30L, RaciRole.C)).isEqualTo(DocumentAccessLevel.FULL);
    }

    @Test
    void restricted에서_I는_초안이면_NONE_공식문서면_OFFICIAL_ONLY다() {
        Document draft = document(1L, true, DocumentStatus.DRAFT);
        Document official = document(1L, true, DocumentStatus.OFFICIAL);

        assertThat(draft.resolveAccessLevel(40L, RaciRole.I)).isEqualTo(DocumentAccessLevel.NONE);
        assertThat(official.resolveAccessLevel(40L, RaciRole.I)).isEqualTo(DocumentAccessLevel.OFFICIAL_ONLY);
    }

    @Test
    void restricted에서_역할이_없으면_NONE이다() {
        Document document = document(1L, true, DocumentStatus.OFFICIAL);

        assertThat(document.resolveAccessLevel(50L, null)).isEqualTo(DocumentAccessLevel.NONE);
    }

    @Test
    void RACI_배정이_있으면_restricted로_전환하고_없으면_원복한다() {
        Document document = document(1L, false, DocumentStatus.DRAFT);

        document.applyRaciAssignment(true);
        assertThat(document.isRestricted()).isTrue();

        document.applyRaciAssignment(false);
        assertThat(document.isRestricted()).isFalse();
    }
}
