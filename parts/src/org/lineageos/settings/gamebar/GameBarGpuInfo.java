/*
 * Copyright (C) 2025 kenway214
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.gamebar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GameBarGpuInfo {

    private static final String GPU_USAGE_PATH = "/sys/kernel/ged/hal/gpu_utilization";
    private static final String GPU_CLOCK_PATH = "/sys/kernel/ged/hal/current_freqency";
    private static final String GPU_TEMP_PATH  = "/sys/class/thermal/thermal_zone1/temp"; // mtktsAP

    public static String getGpuUsage() {
        String line = readLine(GPU_USAGE_PATH);
        if (line == null || line.trim().isEmpty()) {
            return "N/A";
        }

        // Split by whitespace to handle multi-token outputs (e.g., "0 0 100")
        String[] tokens = line.trim().split("\\s+");
        if (tokens.length == 0) {
            return "N/A";
        }

        String rawVal = tokens[0].replace("%", "").trim();
        try {
            int val = Integer.parseInt(rawVal);
            return String.valueOf(val);
        } catch (NumberFormatException e) {
            return "N/A";
        }
    }

    public static String getGpuClock() {
        String line = readLine(GPU_CLOCK_PATH);
        if (line == null || line.trim().isEmpty()) {
            return "N/A";
        }

        String[] tokens = line.trim().split("\\s+");
        String khzStr = null;

        // Take second token if present ("31 299000"), else fallback to single token ("299000")
        if (tokens.length >= 2) {
            khzStr = tokens[1];
        } else if (tokens.length == 1) {
            khzStr = tokens[0];
        }

        if (khzStr == null) {
            return "N/A";
        }

        try {
            long khz = Long.parseLong(khzStr);
            long mhz = khz / 1_000;
            return String.valueOf(mhz);
        } catch (NumberFormatException e) {
            return "N/A";
        }
    }

    public static String getGpuTemp() {
        String line = readLine(GPU_TEMP_PATH);
        if (line == null) {
            return "N/A";
        }
        line = line.trim();
        try {
            float raw = Float.parseFloat(line);
            float c   = raw / 1000f;
            return String.format("%.1f", c);
        } catch (NumberFormatException e) {
            return "N/A";
        }
    }

    private static String readLine(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
