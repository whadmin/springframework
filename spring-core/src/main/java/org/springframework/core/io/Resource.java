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
 * ���ڴ�ʵ�����͵ĵײ���Դ�������ļ�����·����Դ���г������Դ�������Ľӿ�
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
	 * ���ص�ǰResource����ĵײ���Դ�Ƿ���ڣ�true��ʾ����
	 */
	boolean exists();

	/**
	 * ���ص�ǰResource����ĵײ���Դ�Ƿ�ɶ���true��ʾ�ɶ���
	 */
	boolean isReadable();

	/**
	 * ���ص�ǰResource����ĵײ���Դ�Ƿ��Ѿ��򿪣��������true����ֻ�ܱ���ȡһ��Ȼ��ر��Ա�����Դй¶��������Resourceʵ��һ�㷵��false��
	 */
	boolean isOpen();

	/**
	 * �����ǰResource����ĵײ���Դ����java.util.URL�����򷵻ظ�URL
	 */
	URL getURL() throws IOException;

	/**
	 * �����ǰResource����ĵײ���Դ����java.util.URI�����򷵻ظ�URI
	 */
	URI getURI() throws IOException;

	/**
	 * �����ǰResource����ĵײ���Դ����java.io.File�����򷵻ظ�File
	 */
	File getFile() throws IOException;

	/**
	 * ȷ������Դ�����ݳ��ȡ�
	 */
	long contentLength() throws IOException;

	/**
	 * ���ص�ǰResource����ĵײ���Դ������޸�ʱ�䡣
	 */
	long lastModified() throws IOException;

	/**
	 * ���ڴ�������ڵ�ǰResource����ĵײ���Դ����Դ�����統ǰResource�����ļ���Դ��d:/test/����createRelative����test.txt���������ر��ļ���Դ��d:/test/test.txt��Resource��Դ��
	 */
	Resource createRelative(String relativePath) throws IOException;

	/**
	 * ���ص�ǰResource����ĵײ��ļ���Դ���ļ�·��������File��Դ��file://d:/test.txt�������ء�d:/test.txt������URL��Դhttp://www.javass.cn�����ء�������Ϊֻ�����ļ�·����
	 */
	String getFilename();

	/**
	 * ���ش���Դ�����������ڴ�����Դʱ�Ĵ������
	 */
	String getDescription();

}
