/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bson.BsonArray
 *  org.bson.BsonBoolean
 *  org.bson.BsonDecimal128
 *  org.bson.BsonDocument
 *  org.bson.BsonDouble
 *  org.bson.BsonInt32
 *  org.bson.BsonInt64
 *  org.bson.BsonNull
 *  org.bson.BsonNumber
 *  org.bson.BsonString
 *  org.bson.BsonValue
 *  org.bson.types.Decimal128
 */
package org.Ev0Mods.plugin.api.util;

import java.math.BigDecimal;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDecimal128;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonNumber;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.types.Decimal128;

public class BsonHelper {
    public static BsonValue parseValue(String input) {
        if ((input = input.trim()).isEmpty() || input.equalsIgnoreCase("null")) {
            return BsonNull.VALUE;
        }
        char first = input.charAt(0);
        if (first == '{') {
            return BsonDocument.parse((String)input);
        }
        if (first == '[') {
            return BsonArray.parse((String)input);
        }
        if (Character.isDigit(first) || first == '-' || first == '+') {
            return BsonHelper.parseNumber(input);
        }
        if (input.equalsIgnoreCase("true")) {
            return BsonBoolean.TRUE;
        }
        if (input.equalsIgnoreCase("false")) {
            return BsonBoolean.FALSE;
        }
        return new BsonString(input);
    }

    public static BsonNumber parseNumber(String string) {
        try {
            if (string.contains(".") || string.contains("e") || string.contains("E")) {
                return new BsonDouble(Double.parseDouble(string));
            }
            long l = Long.parseLong(string);
            if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                return new BsonInt32((int)l);
            }
            return new BsonInt64(l);
        }
        catch (NumberFormatException e) {
            return new BsonDecimal128(new Decimal128(new BigDecimal(string)));
        }
    }
}

