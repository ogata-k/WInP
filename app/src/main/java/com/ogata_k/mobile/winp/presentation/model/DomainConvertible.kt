package com.ogata_k.mobile.winp.presentation.model

import java.util.UUID

/**
 * domainパッケージにあるモデルに変換する機能を備えたインターフェース
 */
interface ToDomain<Domain> {
    /**
     * domainパッケージにあるモデルに変換する
     */
    fun toDomainModel(): Domain
}

/**
 * domainパッケージにあるモデルから変換する機能を備えたインターフェース
 */
interface FromDomain<Domain, Presentation> {
    /**
     * domainパッケージにあるモデルから変換する
     */
    fun fromDomainModel(domain: Domain): Presentation
}

/**
 * domainパッケージにあるモデルから変換する機能を備えたインターフェース
 */
interface FromDomainWithUuid<Domain, Presentation> {
    /**
     * domainパッケージにあるモデルから変換する
     */
    fun fromDomainModel(domain: Domain, uuid: UUID): Presentation
}