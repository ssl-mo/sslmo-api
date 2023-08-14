package com.sslmo.database.sql_types

import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.nio.ByteBuffer
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types
import java.util.*

fun <E : Any> BaseTable<E>.uuid(name: String): Column<UUID> = registerColumn(name, UUIDSqlType)

object UUIDSqlType : SqlType<UUID>(Types.VARBINARY, typeName = "uuid") {
    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: UUID) {
        val b = ByteBuffer.wrap(ByteArray(16))
        b.putLong(parameter.mostSignificantBits)
        b.putLong(parameter.leastSignificantBits)
        ps.setBytes(index, b.array())
    }

    override fun doGetResult(rs: ResultSet, index: Int): UUID? = rs.getBytes(index)?.let {
        val byteBuffer = ByteBuffer.wrap(it)
        val high = byteBuffer.getLong()
        val low = byteBuffer.getLong()
        return UUID(high, low)
    }
}

