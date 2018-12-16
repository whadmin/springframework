/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * 用于从实际类型的底层资源（例如文件或类路径资源）中抽象的资源描述符的接口
 *
 *
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see #getInputStream()
 * @see #getURL()
 * @see #getURI()
 * @see #getFile()
 * @see WritableResource
 * @see ContextResource
 * @see UrlResource
 * @see ClassPathResource
 * @see FileSystemResource
 * @see PathResource
 * @see ByteArrayResource
 * @see InputStreamResource
 */
public interface Resource extends InputStreamSource {

	/**
	 * 返回当前Resource代表的底层资源是否存在，true表示存在
	 */
	boolean exists();

	/**
	 * 返回当前Resource代表的底层资源是否可读，true表示可读。
	 */
	boolean isReadable();

	/**
	 * 返回当前Resource代表的底层资源是否已经打开，如果返回true，则只能被读取一次然后关闭以避免资源泄露；常见的Resource实现一般返回false。
	 */
	boolean isOpen();

	/**
	 * 如果当前Resource代表的底层资源能由java.util.URL代表，则返回该URL
	 */
	URL getURL() throws IOException;

	/**
	 * 如果当前Resource代表的底层资源能由java.util.URI代表，则返回该URI
	 */
	URI getURI() throws IOException;

	/**
	 * 如果当前Resource代表的底层资源能由java.io.File代表，则返回该File
	 */
	File getFile() throws IOException;

	/**
	 * 确定此资源的内容长度。
	 */
	long contentLength() throws IOException;

	/**
	 * 返回当前Resource代表的底层资源的最后修改时间。
	 */
	long lastModified() throws IOException;

	/**
	 * 用于创建相对于当前Resource代表的底层资源的资源，比如当前Resource代表文件资源“d:/test/”则createRelative（“test.txt”）将返回表文件资源“d:/test/test.txt”Resource资源。
	 */
	Resource createRelative(String relativePath) throws IOException;

	/**
	 * 返回当前Resource代表的底层文件资源的文件路径，比如File资源“file://d:/test.txt”将返回“d:/test.txt”，而URL资源http://www.javass.cn将返回“”，因为只返回文件路径。
	 */
	String getFilename();

	/**
	 * 返回此资源的描述，用于处理资源时的错误输出
	 */
	String getDescription();

}
