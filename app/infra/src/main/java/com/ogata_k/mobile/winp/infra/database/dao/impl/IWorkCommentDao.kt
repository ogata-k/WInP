package com.ogata_k.mobile.winp.infra.database.dao.impl

import androidx.room.withTransaction
import com.ogata_k.mobile.winp.infra.database.AppDatabase
import com.ogata_k.mobile.winp.infra.database.dao.WorkCommentDao
import com.ogata_k.mobile.winp.infra.database.entity.WorkComment
import java.time.OffsetDateTime
import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkCommentDao as DomainWorkCommentDao
import com.ogata_k.mobile.winp.domain.model.work.WorkComment as DomainWorkComment

class IWorkCommentDao(private val db: AppDatabase, private val dao: WorkCommentDao) :
    DomainWorkCommentDao {
    override suspend fun fetchAllWorkCommentsOrderByCreatedAtDesc(workId: Long): List<DomainWorkComment> {
        return dao.fetchAllWorkCommentsOrderByCreatedAtDesc(workId).map {
            toDomainWorkComment(it)
        }
    }

    override suspend fun insertWorkComment(comment: DomainWorkComment) {
        db.withTransaction {
            dao.insertWorkComment(toInfraWorkComment(comment))
        }
    }

    override suspend fun updateWorkComment(
        workCommentId: Long,
        comment: String,
        modifiedAt: OffsetDateTime
    ) {
        db.withTransaction {
            dao.updateWorkComment(workCommentId, comment, modifiedAt)
        }
    }
}

private fun toDomainWorkComment(workComment: WorkComment): DomainWorkComment {
    return DomainWorkComment(
        workCommentId = workComment.workCommentId,
        workId = workComment.workId,
        comment = workComment.comment,
        modifiedAt = workComment.modifiedAt,
        createdAt = workComment.createdAt,
    )
}

private fun toInfraWorkComment(workComment: DomainWorkComment): WorkComment {
    return WorkComment(
        workCommentId = workComment.workCommentId,
        workId = workComment.workId,
        comment = workComment.comment,
        modifiedAt = workComment.modifiedAt,
        createdAt = workComment.createdAt,
    )
}