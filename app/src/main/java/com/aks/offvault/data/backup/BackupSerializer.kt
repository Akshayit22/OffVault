package com.aks.offvault.data.backup

import com.aks.offvault.data.model.Card
import com.aks.offvault.data.model.CardType
import com.aks.offvault.data.model.Document
import com.aks.offvault.data.model.LoginDetail
import com.aks.offvault.data.model.Other
import org.json.JSONArray
import org.json.JSONObject

data class BackupData(
    val cards: List<Card>,
    val documents: List<Document>,
    val loginDetails: List<LoginDetail>,
    val others: List<Other>
)

object BackupSerializer {

    private const val VERSION = 1

    fun serialize(
        cards: List<Card>,
        documents: List<Document>,
        loginDetails: List<LoginDetail>,
        others: List<Other>
    ): String {
        val root = JSONObject()
        root.put("version", VERSION)
        root.put("exported_at", System.currentTimeMillis())

        val cardsArr = JSONArray()
        cards.forEach { c ->
            cardsArr.put(JSONObject().apply {
                put("label", c.label)
                put("card_number", c.cardNumber)
                put("expiry_month", c.expiryMonth)
                put("expiry_year", c.expiryYear)
                put("card_type", c.cardType.name)
                put("bank_name", c.bankName)
                put("cvv", c.cvv)
                put("notes", c.notes)
                put("created_at", c.createdAt)
                put("updated_at", c.updatedAt)
            })
        }
        root.put("cards", cardsArr)

        val docsArr = JSONArray()
        documents.forEach { d ->
            docsArr.put(JSONObject().apply {
                put("title", d.title)
                put("document_id", d.documentId)
                put("info", d.info)
                put("created_at", d.createdAt)
                put("updated_at", d.updatedAt)
            })
        }
        root.put("documents", docsArr)

        val loginsArr = JSONArray()
        loginDetails.forEach { l ->
            loginsArr.put(JSONObject().apply {
                put("title", l.title)
                put("username", l.username)
                put("password", l.password)
                put("info", l.info)
                put("created_at", l.createdAt)
                put("updated_at", l.updatedAt)
            })
        }
        root.put("login_details", loginsArr)

        val othersArr = JSONArray()
        others.forEach { o ->
            othersArr.put(JSONObject().apply {
                put("title", o.title)
                put("info", o.info)
                put("created_at", o.createdAt)
                put("updated_at", o.updatedAt)
            })
        }
        root.put("others", othersArr)

        return root.toString()
    }

    fun deserialize(json: String): BackupData {
        val root = JSONObject(json)

        val cards = mutableListOf<Card>()
        val cardsArr = root.getJSONArray("cards")
        for (i in 0 until cardsArr.length()) {
            val o = cardsArr.getJSONObject(i)
            cards.add(Card(
                id = 0,
                label = o.getString("label"),
                cardNumber = o.getString("card_number"),
                expiryMonth = o.getString("expiry_month"),
                expiryYear = o.getString("expiry_year"),
                cardType = CardType.valueOf(o.getString("card_type")),
                bankName = o.getString("bank_name"),
                cvv = o.getString("cvv"),
                notes = o.getString("notes"),
                createdAt = o.getLong("created_at"),
                updatedAt = o.getLong("updated_at")
            ))
        }

        val documents = mutableListOf<Document>()
        val docsArr = root.getJSONArray("documents")
        for (i in 0 until docsArr.length()) {
            val o = docsArr.getJSONObject(i)
            documents.add(Document(
                id = 0,
                title = o.getString("title"),
                documentId = o.getString("document_id"),
                info = o.getString("info"),
                createdAt = o.getLong("created_at"),
                updatedAt = o.getLong("updated_at")
            ))
        }

        val loginDetails = mutableListOf<LoginDetail>()
        val loginsArr = root.getJSONArray("login_details")
        for (i in 0 until loginsArr.length()) {
            val o = loginsArr.getJSONObject(i)
            loginDetails.add(LoginDetail(
                id = 0,
                title = o.getString("title"),
                username = o.getString("username"),
                password = o.getString("password"),
                info = o.getString("info"),
                createdAt = o.getLong("created_at"),
                updatedAt = o.getLong("updated_at")
            ))
        }

        val others = mutableListOf<Other>()
        val othersArr = root.getJSONArray("others")
        for (i in 0 until othersArr.length()) {
            val o = othersArr.getJSONObject(i)
            others.add(Other(
                id = 0,
                title = o.getString("title"),
                info = o.getString("info"),
                createdAt = o.getLong("created_at"),
                updatedAt = o.getLong("updated_at")
            ))
        }

        return BackupData(cards, documents, loginDetails, others)
    }
}
