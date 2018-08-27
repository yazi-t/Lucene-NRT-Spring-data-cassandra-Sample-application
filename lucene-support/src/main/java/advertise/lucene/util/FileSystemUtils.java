/*
 * This software is licensed under the Apache 2 license, quoted below.
 *
 * Copyright 2018 Yasitha Thilakaratne
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package advertise.lucene.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The {@link FileSystemUtils} class is containing utility methods for file
 * system access.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public class FileSystemUtils {

    /**
     * Checks whether the given directory exists in the file system.
     *
     * @param directory {@link File} to check
     * @return true if exists
     */
    public static boolean doesDirectoryExist(File directory) {
        return directory != null && directory.exists() && directory.isDirectory();
    }

    /**
     * Removes all directories and files in given path recursively.
     *
     * @param folder {@link File} to clean
     * @throws IOException
     */
    public static void clean(Path folder) throws IOException {
        Files.walk(folder).filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
    }
}
