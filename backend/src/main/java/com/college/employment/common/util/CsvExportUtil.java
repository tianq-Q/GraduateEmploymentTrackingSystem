/*
 * Copyright (c) 2026 EmploymentTracking Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.college.employment.common.util;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
 * CSV 导出工具（零依赖，GBK 编码兼容 Excel 中文）。
 * 满足文档「二基本信息模块」2.3 导出按钮需求（导出毕业生/就业统计报表）。
 */
public final class CsvExportUtil {

    private CsvExportUtil() {}

    /**
     * 写出 CSV 文件到响应（带 BOM + GBK，Excel 可直接打开）。
     *
     * @param response   HttpServletResponse
     * @param fileName   文件名（无需后缀）
     * @param headers    CSV 表头
     * @param rows       数据行（每行已是逗号分隔的单元格文本）
     */
    public static void writeCsv(HttpServletResponse response, String fileName,
                               List<String> headers, List<String> rows) throws Exception {
        response.setContentType("text/csv;charset=GBK");
        String encodedName = URLEncoder.encode(fileName + ".csv", "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedName);
        response.setCharacterEncoding("GBK");

        try (OutputStream os = response.getOutputStream()) {
            // BOM 头，保证 Excel 识别 UTF-8/GBK 中文
            os.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
            StringBuilder sb = new StringBuilder();
            sb.append(String.join(",", headers)).append("\r\n");
            for (String row : rows) {
                sb.append(row).append("\r\n");
            }
            os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
    }

    /** 对单个单元格做 CSV 转义（含逗号/引号/换行时加引号）。 */
    public static String cell(Object value) {
        if (value == null) {
            return "";
        }
        String s = String.valueOf(value);
        if (s.contains(",") || s.contains("\"") || s.contains("\r") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
