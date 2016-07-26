/*
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
package com.teradata.benchto.driver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public class FilterUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterUtils.class);

    /**
     * @return Predicate which returns true when path contains any of given string
     */
    public static Predicate<Path> pathContainsAny(List<String> strings)
    {
        return path -> {
            boolean included = strings.stream()
                    .anyMatch(wildcardMatcher -> path.toString().contains(wildcardMatcher));
            return included;
        };
    }
}
