package com.ot.util;

import com.ot.annotation.ValidObjectId;
import org.bson.types.ObjectId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ObjectIdValidator implements ConstraintValidator<ValidObjectId, String> {

    @Override
    public void initialize(ValidObjectId constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // null 또는 빈 문자열은 허용
        }

        try {
            new ObjectId(value); // ObjectId 생성 시 예외가 발생하지 않으면 유효한 ObjectId
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
