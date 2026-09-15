/*
 * MIT License
 *
 * Copyright (c) 2026 Employment Tracking System
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.college.employment.infrastructure.repository;

import com.college.employment.domain.model.EmploymentRecord;
import com.college.employment.domain.repository.EmploymentRecordRepository;
import com.college.employment.infrastructure.mapper.EmploymentRecordMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmploymentRecordRepositoryImpl implements EmploymentRecordRepository {

    private final EmploymentRecordMapper mapper;

    @Override
    public EmploymentRecord save(EmploymentRecord record) {
        if (record.getId() == null) {
            mapper.insert(record);
        } else {
            mapper.updateById(record);
        }
        return record;
    }

    @Override
    public Optional<EmploymentRecord> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id));
    }

    @Override
    public List<EmploymentRecord> findByGraduateId(Long graduateId) {
        return mapper.findByGraduateId(graduateId);
    }

    @Override
    public Optional<EmploymentRecord> findLatestByGraduateId(Long graduateId) {
        return Optional.ofNullable(mapper.findLatestByGraduateId(graduateId));
    }

    @Override
    public void updateById(EmploymentRecord record) {
        mapper.updateById(record);
    }
}
