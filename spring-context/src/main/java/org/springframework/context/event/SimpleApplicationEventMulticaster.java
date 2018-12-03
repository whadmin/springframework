/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.context.event;

import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.util.ErrorHandler;

/**
 * ��ʵ��{@link ApplicationEventMulticaster}�ӿڡ�
 *
 * <p>�������¼��ಥ��������ע��������������������������Ժ������ǲ���??��Ȥ���¼���*������ͨ����Դ�����¼�����ִ����Ӧ��{@code instanceof} *��顣
 *
 * <p>Ĭ������£��ڵ����߳��е��������������� , *�����������������ֹ����Ӧ�ó����Σ�գ�*����������С�Ŀ�����, ָ����������ִ�г�����ʹ*�������ڲ�ͬ���߳���ִ�У�������̳߳���ִ�С�
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @see #setTaskExecutor
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

	private Executor taskExecutor;

	private ErrorHandler errorHandler;


	/**
	 * ����һ���µ�SimpleApplicationEventMulticaster��
	 */
	public SimpleApplicationEventMulticaster() {
	}

	/**
	 * Ϊ�����ڲ�����bean��������һ���µ�SimpleApplicationEventMulticaster��
	 */
	public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
		setBeanFactory(beanFactory);
	}


	/**
	 * �����Զ���ִ�г���ͨ����{@link org.springframework.core.task.TaskExecutor}��*���첽֪ͨ����ÿ����������
	 * <p>Ĭ�ϵ�Ч��{@link org.springframework.core.task.SyncTaskExecutor}��
	 * �ڵ����߳���ͬ��ִ�������������� ,
	 * <p>�����ڴ�ָ��һ���첽����ִ�������Ա�����������������ִ��֮ǰ������*�����ߡ�, ���ǣ���ע���첽*ִ�в����������ߵ��߳������ģ����������*���������������TaskExecutor��ȷ֧�ִ˲�����
	 * @see org.springframework.core.task.SyncTaskExecutor
	 * @see org.springframework.core.task.SimpleAsyncTaskExecutor
	 */
	public void setTaskExecutor(Executor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * ���ص�ǰ����ִ�г���
	 */
	protected Executor getTaskExecutor() {
		return this.taskExecutor;
	}

	/**
	 * ������������׳��쳣*��������{@link ErrorHandler}�Խ��е��á�
	 * @since 4.1
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * ���ش˶ಥ���ĵ�ǰ���������{@link ErrorHandler}
	 * @since 4.1
	 */
	protected ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}


	/**
	 * �����¼�
	 */
	@Override
	public void multicastEvent(ApplicationEvent event) {
		multicastEvent(event, resolveDefaultEventType(event));
	}


	/**
	 * �����¼�
	 */
	@Override
	public void multicastEvent(final ApplicationEvent event, ResolvableType eventType) {
		ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
		for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
			Executor executor = getTaskExecutor();
			if (executor != null) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						invokeListener(listener, event);
					}
				});
			}
			else {
				invokeListener(listener, event);
			}
		}
	}

	/**
	 *  ��ȡ�¼�������
	 * */
	private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
		return ResolvableType.forInstance(event);
	}

	/**
	 * ʹ�ø������¼����ø�������������
	 * @param listener the ApplicationListener to invoke
	 * @param event the current event to propagate
	 * @since 4.1
	 */
	protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
		ErrorHandler errorHandler = getErrorHandler();
		if (errorHandler != null) {
			try {
				doInvokeListener(listener, event);
			}
			catch (Throwable err) {
				errorHandler.handleError(err);
			}
		}
		else {
			doInvokeListener(listener, event);
		}
	}

	private void doInvokeListener(ApplicationListener listener, ApplicationEvent event) {
		try {
			listener.onApplicationEvent(event);
		}
		catch (ClassCastException ex) {
			String msg = ex.getMessage();
			if (msg == null || matchesClassCastMessage(msg, event.getClass())) {
				// Possibly a lambda-defined listener which we could not resolve the generic event type for
				// -> let's suppress the exception and just log a debug message.
				Log logger = LogFactory.getLog(getClass());
				if (logger.isDebugEnabled()) {
					logger.debug("Non-matching event type for listener: " + listener, ex);
				}
			}
			else {
				throw ex;
			}
		}
	}

	private boolean matchesClassCastMessage(String classCastMessage, Class<?> eventClass) {
		// On Java 8, the message starts with the class name: "java.lang.String cannot be cast..."
		if (classCastMessage.startsWith(eventClass.getName())) {
			return true;
		}
		// On Java 11, the message starts with "class ..." a.k.a. Class.toString()
		if (classCastMessage.startsWith(eventClass.toString())) {
			return true;
		}
		// On Java 9, the message used to contain the module name: "java.base/java.lang.String cannot be cast..."
		int moduleSeparatorIndex = classCastMessage.indexOf('/');
		if (moduleSeparatorIndex != -1 && classCastMessage.startsWith(eventClass.getName(), moduleSeparatorIndex + 1)) {
			return true;
		}
		// Assuming an unrelated class cast failure...
		return false;
	}

}
