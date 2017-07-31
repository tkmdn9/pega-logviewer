/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;

public class EXCP0001ReportModel extends AlertMessageReportModel {

	private static final long serialVersionUID = -8889727175209305065L;

	private static final Log4j2Helper LOG = new Log4j2Helper(EXCP0001ReportModel.class);

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	private Pattern pattern;

	private Pattern exceptionPattern;

	public EXCP0001ReportModel(long thresholdKPI, String kpiUnit, AlertLogEntryModel alertLogEntryModel) {

		super("EXCP0001", thresholdKPI, kpiUnit, alertLogEntryModel);

		String regex = "\\]\\[STACK\\]\\[(.*)\\]";
		pattern = Pattern.compile(regex);

		String exceptionRegex = "([\\w\\.]*(Exception|Error))[\\s:<;]";
		exceptionPattern = Pattern.compile(exceptionRegex);
	}

	@Override
	protected List<AlertBoxAndWhiskerReportColumn> getAlertMessageReportColumnList() {

		if (alertMessageReportColumnList == null) {
			alertMessageReportColumnList = new ArrayList<AlertBoxAndWhiskerReportColumn>();

			String displayName;
			int prefColWidth;
			int hAlignment;
			boolean filterable;
			AlertBoxAndWhiskerReportColumn amReportColumn = null;

			// first column data is the key
			displayName = "Exception";
			prefColWidth = 500;
			hAlignment = SwingConstants.LEFT;
			filterable = true;
			amReportColumn = new AlertBoxAndWhiskerReportColumn(AlertBoxAndWhiskerReportColumn.KEY, displayName, prefColWidth, hAlignment, filterable);
			alertMessageReportColumnList.add(amReportColumn);

			List<AlertBoxAndWhiskerReportColumn> defaultAlertMessageReportColumnList = AlertBoxAndWhiskerReportColumn
					.getDefaultAlertMessageReportColumnList();

			alertMessageReportColumnList.addAll(defaultAlertMessageReportColumnList);
		}

		return alertMessageReportColumnList;
	}

	@Override
	public String getAlertMessageReportEntryKey(AlertLogEntry alertLogEntry, ArrayList<String> logEntryValueList) {

		String alertMessageReportEntryKey = null;

		AlertLogEntryModel alertLogEntryModel = getAlertLogEntryModel();

		List<String> logEntryColumnList = alertLogEntryModel.getLogEntryColumnList();

		int messageIndex = logEntryColumnList.indexOf(LogEntryColumn.MESSAGE.getColumnId());
		String message = logEntryValueList.get(messageIndex);

		Matcher patternMatcher = pattern.matcher(message);
		boolean matches = patternMatcher.find();

		if (matches) {

			String stack = patternMatcher.group(1);

			Matcher exceptionPatternMatcher = exceptionPattern.matcher(stack);
			matches = exceptionPatternMatcher.find();

			if (matches) {
				alertMessageReportEntryKey = exceptionPatternMatcher.group(1).trim();
			} else if (stack.startsWith("java.lang.Throwable")) {
				alertMessageReportEntryKey = "java.lang.Throwable";
			}
		}

		if (alertMessageReportEntryKey == null) {
			LOG.info("EXCP0001ReportModel - Could'nt match - [" + message + "]");
		}

		return alertMessageReportEntryKey;
	}

	public static void main(String[] args) {

		long before = System.currentTimeMillis();
		String message = "[MSG][Error saving][STACK][com.pega.pegarules.pub.database.DatabaseException: ORA-01013: user requested cancel of current operation   DatabaseException caused by prior exception: java.sql.BatchUpdateException: ORA-01013: user requested cancel of current operation   | SQL Code: 1013 | SQL State: 72000  DatabaseException caused by prior exception: java.sql.SQLTimeoutException: ORA-01013: user requested cancel of current operation   | SQL Code: 1013 | SQL State: 72000   From: (BB811181F0CF3D509F5944B72F1B5A691:(ManagementDaemon))    SQL: INSERT INTO DATA.pr_sys_statusdetails (pzInsKey , pxCommitDateTime , PXACTIVETHREADCOUNT , PXAGENTCOUNT , PXAVGHTTPRESPTIME , PXCACHEENABLED , PXCACHESIZE , PXCACHESIZEPERCENT , PXCONNECTIONSTRING , PXCREATEDATETIME , PXDATABASECONNECTIONCOUNT , PXGARBAGECOLLECTIONCOUNT , PXINSNAME , PXLASTPULSE , PXLISTENERCOUNT , PXMEMORYFREE , PXMEMORYMAX , PXMEMORYTOTAL , PXMEMORYUSED , PXMEMORYUSEDPERCENT , PXNODEDESCRIPTION , PXNODERUNSTATE , PXOBJCLASS , PXPEGA0001 , PXPEGA0005 , PXPEGA0010 , PXPEGA0011 , PXPEGA0019 , PXPEGA0020 , PXPROCESSCPUTIME , PXPROCESSCPUUSAGE , PXREQUESTORCOUNT , PXSAVEDATETIME , PXSNAPSHOTTIME , PXSYSTEMSTART , PXTYPE , PXUPDATEDATETIME , PYNODENAME , PYSYSNODEID , PYSYSTEMNAME) VALUES (? , CURRENT_TIMESTAMP , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ?)   Caused by SQL Problems.  Problem #1, SQLState 72000, Error code 1013: java.sql.BatchUpdateException: ORA-01013: user requested cancel of current operation   Problem #2, SQLState 72000, Error code 1013: java.sql.SQLTimeoutException: ORA-01013: user requested cancel of current operation    at com.pega.pegarules.data.internal.access.ExceptionInformation.createExceptionDueToDBFailure(ExceptionInformation.java:299)   at com.pega.pegarules.data.internal.access.ConnectionStatementStore.executeBatchForAllStatements(ConnectionStatementStore.java:200)   at com.pega.pegarules.data.internal.access.ThreadConnectionStoreImpl.executeOutstandingBatches(ThreadConnectionStoreImpl.java:271)   at com.pega.pegarules.data.internal.access.DatabaseImpl.attemptToProcessUpdates(DatabaseImpl.java:2758)   at com.pega.pegarules.data.internal.access.DatabaseImpl.processUpdates(DatabaseImpl.java:2400)   at com.pega.pegarules.data.internal.access.Saver.save(Saver.java:651)   at com.pega.pegarules.data.internal.access.DatabaseImpl.save(DatabaseImpl.java:5192)   at com.pega.pegarules.data.internal.access.DatabaseImpl.save(DatabaseImpl.java:5178)   at com.pega.pegarules.data.internal.access.DatabaseImpl.save(DatabaseImpl.java:5169)   at com.pega.pegarules.management.internal.PRManagementProviderImpl.insertRowsToSysManagementDB(PRManagementProviderImpl.java:214)   at com.pega.pegarules.management.internal.PRManagementProviderImpl.getStatusClipboard(PRManagementProviderImpl.java:99)   at com.pega.pegarules.monitor.internal.context.ManagementDaemonImpl.performManangementActions(ManagementDaemonImpl.java:290)   at sun.reflect.GeneratedMethodAccessor113.invoke(Unknown Source)   at sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)   at java.lang.reflect.Method.invoke(Unknown Source)   at com.pega.pegarules.session.internal.PRSessionProviderImpl.performTargetActionWithLock(PRSessionProviderImpl.java:1277)   at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:1015)   at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:848)   at com.pega.pegarules.session.external.async.AbstractDaemon.performProcessing(AbstractDaemon.java:317)   at com.pega.pegarules.session.external.async.AbstractDaemon.run(AbstractDaemon.java:262)   at java.lang.Thread.run(Unknown Source)  Caused by: java.sql.BatchUpdateException: ORA-01013: user requested cancel of current operation    at oracle.jdbc.driver.OraclePreparedStatement.executeBatch(OraclePreparedStatement.java:11190)   at oracle.jdbc.driver.OracleStatementWrapper.executeBatch(OracleStatementWrapper.java:244)   at org.apache.tomcat.dbcp.dbcp.DelegatingStatement.executeBatch(DelegatingStatement.java:297)   at org.apache.tomcat.dbcp.dbcp.DelegatingStatement.executeBatch(DelegatingStatement.java:297)   at com.pega.pegarules.data.internal.access.DatabasePreparedStatementImpl.executeBatch(DatabasePreparedStatementImpl.java:535)   at com.pega.pegarules.data.internal.access.ConnectionStatementStore.executeBatchForAllStatements(ConnectionStatementStore.java:198)   ... 19 more  ]";
		String message1 = "[MSG][The element at the top of the stack: ][STACK][java.lang.Throwable<CR><CR>	at com.pega.pegarules.monitor.internal.database.ReporterImpl.<init>(ReporterImpl.java:427)<CR><CR>	at com.pega.pegarules.monitor.internal.database.ReporterStackImpl.push(ReporterStackImpl.java:89)<CR><CR>	at com.pega.pegarules.data.internal.access.DatabaseImpl.commit(DatabaseImpl.java:2033)<CR><CR>	at com.pegarules.generated.activity.ra_action_commitwitherrorhandling_6c6c3f21ead5ea2a97cdc30349237e37.step4_circum0(ra_action_commitwitherrorhandling_6c6c3f21ead5ea2a97cdc30349237e37.java:492)<CR><CR>	at com.pegarules.generated.activity.ra_action_commitwitherrorhandling_6c6c3f21ead5ea2a97cdc30349237e37.perform(ra_action_commitwitherrorhandling_6c6c3f21ead5ea2a97cdc30349237e37.java:120)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3505)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.Executable.invokeActivity(Executable.java:10563)<CR><CR>	at com.pegarules.generated.activity.ra_action_workcommit_cae6addc5923a8c1402dc635b7fbae23.step3_circum0(ra_action_workcommit_cae6addc5923a8c1402dc635b7fbae23.java:415)<CR><CR>	at com.pegarules.generated.activity.ra_action_workcommit_cae6addc5923a8c1402dc635b7fbae23.perform(ra_action_workcommit_cae6addc5923a8c1402dc635b7fbae23.java:103)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3505)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.Executable.invokeActivity(Executable.java:10563)<CR><CR>	at com.pegarules.generated.activity.ra_action_add_5fa4be2f584be40d9e1b50cf1962a8d8.step5_circum0(ra_action_add_5fa4be2f584be40d9e1b50cf1962a8d8.java:694)<CR><CR>	at com.pegarules.generated.activity.ra_action_add_5fa4be2f584be40d9e1b50cf1962a8d8.perform(ra_action_add_5fa4be2f584be40d9e1b50cf1962a8d8.java:140)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3505)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.base.ThreadRunner.runActivitiesAlt(ThreadRunner.java:646)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.PRThreadImpl.runActivitiesAlt(PRThreadImpl.java:461)<CR><CR>	at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.runActivities(HttpAPI.java:3358)<CR><CR>	at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRequestInner(EngineAPI.java:385)<CR><CR>	at sun.reflect.GeneratedMethodAccessor44.invoke(Unknown Source)<CR><CR>	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)<CR><CR>	at java.lang.reflect.Method.invoke(Method.java:497)<CR><CR>	at com.pega.pegarules.session.internal.PRSessionProviderImpl.performTargetActionWithLock(PRSessionProviderImpl.java:1270)<CR><CR>	at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:1008)<CR><CR>	at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:841)<CR><CR>	at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRequest(EngineAPI.java:331)<CR><CR>	at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.invoke(HttpAPI.java:852)<CR><CR>	at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl._invokeEngine_privact(EngineImpl.java:315)<CR><CR>	at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:263)<CR><CR>	at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:240)<CR><CR>	at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngineInner(JNDIEnvironment.java:278)<CR><CR>	at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngine(JNDIEnvironment.java:223)<CR><CR>	at com.pega.pegarules.web.impl.WebStandardImpl.makeEtierRequest(WebStandardImpl.java:574)<CR><CR>	at com.pega.pegarules.web.impl.WebStandardImpl.doPost(WebStandardImpl.java:374)<CR><CR>	at sun.reflect.GeneratedMethodAccessor43.invoke(Unknown Source)<CR><CR>	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)<CR><CR>	at java.lang.reflect.Method.invoke(Method.java:497)<CR><CR>	at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethod(PRBootstrap.java:370)<CR><CR>	at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethodPropagatingThrowable(PRBootstrap.java:411)<CR><CR>	at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethodPropagatingThrowable(AppServerBridgeToPega.java:223)<CR><CR>	at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethod(AppServerBridgeToPega.java:272)<CR><CR>	at com.pega.pegarules.internal.web.servlet.WebStandardBoot.doPost(WebStandardBoot.java:121)<CR><CR>	at javax.servlet.http.HttpServlet.service(HttpServlet.java:754)<CR><CR>	at javax.servlet.http.HttpServlet.service(HttpServlet.java:847)<CR><CR>	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:295)<CR><CR>	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214)<CR><CR>	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:231)<CR><CR>	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:149)<CR><CR>	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:420)<CR><CR>	at org.jboss.as.web.session.ClusteredSessionValve.handleRequest(ClusteredSessionValve.java:134)<CR><CR>	at org.jboss.as.web.session.ClusteredSessionValve.invoke(ClusteredSessionValve.java:99)<CR><CR>	at org.jboss.as.web.session.JvmRouteValve.invoke(JvmRouteValve.java:92)<CR><CR>	at org.jboss.as.web.session.LockingValve.invoke(LockingValve.java:64)<CR><CR>	at org.jboss.as.web.security.SecurityContextAssociationValve.invoke(SecurityContextAssociationValve.java:169)<CR><CR>	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:150)<CR><CR>	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:97)<CR><CR>	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:102)<CR><CR>	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)<CR><CR>	at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:854)<CR><CR>	at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:653)<CR><CR>	at org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:926)<CR><CR>	at java.lang.Thread.run(Thread.java:745)<CR><CR>]";
		String message2 = "[MSG][Section 'ErrorList' execution error on page 'pyWorkPage' of class 'CX-FW-CAREFW-Work-WholeHome-Internet'.][STACK][com.pega.pegarules.pub.context.StaleRequestorError: PRRequestorImpl was explicitly destroyed  at com.pega.pegarules.session.internal.mgmt.PRRequestorImpl.validateUse(PRRequestorImpl.java:337)  at com.pega.pegarules.session.internal.mgmt.PRRequestorImpl.getParent(PRRequestorImpl.java:1669)  at com.pega.pegarules.data.external.clipboard.ClipboardObjectImpl.getParentProperty(ClipboardObjectImpl.java:188)  at com.pega.pegarules.data.internal.clipboard.ClipboardPropertyImpl.getValue(ClipboardPropertyImpl.java:4387)  at com.pega.pegarules.data.internal.clipboard.ClipboardPropertyBase.getValue(ClipboardPropertyBase.java:3172)  at com.pega.pegarules.data.internal.clipboard.ClipboardPropertyImpl.getPageValue(ClipboardPropertyImpl.java:3486)  at com.pega.pegarules.data.internal.clipboard.ClipboardPropertyImpl.getPageValue(ClipboardPropertyImpl.java:3455)  at com.pega.pegarules.data.internal.clipboard.PropertyDataPageWrapper.prGetPropertyNames(PropertyDataPageWrapper.java:414)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.getKeySetFromWrapper(ClipboardPageImpl.java:4952)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.keySet(ClipboardPageImpl.java:4923)  at com.pega.pegarules.data.internal.clipboard.PropertyDataPageWrapper.prGetPropertyNames(PropertyDataPageWrapper.java:418)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.getKeySetFromWrapper(ClipboardPageImpl.java:4952)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.values(ClipboardPageImpl.java:4972)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.values(ClipboardPageImpl.java:4982)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageBase.getMessagesMap(ClipboardPageBase.java:1085)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageBase.getMessagesMap(ClipboardPageBase.java:1098)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageBase.getMessagesMap(ClipboardPageBase.java:1098)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageBase.getMessagesMap(ClipboardPageBase.java:1098)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageBase.getMessagesMapByEntryHandle(ClipboardPageBase.java:1024)  at com.pega.pegarules.data.internal.clipboard.ClipboardPageImpl.getMessagesMapByEntryHandle(ClipboardPageImpl.java:482)  at com.pegarules.generated.html_section.ra_stream_errorlist_7893956e257c0bd2fcbf958e614aa40d.execute(ra_stream_errorlist_7893956e257c0bd2fcbf958e614aa40d.java:120)  at com.pega.pegarules.session.internal.mgmt.StreamBuilderTools.appendStreamKeepProperties(StreamBuilderTools.java:717)  at com.pega.pegarules.session.internal.mgmt.autostreams.IncludeStreamRuntime.getStream(IncludeStreamRuntime.java:332)  at com.pega.pegarules.session.internal.mgmt.autostreams.IncludeStreamRuntime.emitIncludeStreamReference(IncludeStreamRuntime.java:252)  at com.pega.pegarules.session.internal.mgmt.autostreams.AutoStreamRuntimeImpl.emitIncludeStreamReference(AutoStreamRuntimeImpl.java:358)  at com.pegarules.generated.html_section.ra_stream_errors_ea6f8491c5a6feec558b04f314301826.execute(ra_stream_errors_ea6f8491c5a6feec558b04f314301826.java:147)  at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:4033)  at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:3861)  at com.pegarules.generated.html_harness.ra_stream_harnessfail_cc54e2e00e5fa3171adb4ff3b8301fe0.include_1(ra_stream_harnessfail_cc54e2e00e5fa3171adb4ff3b8301fe0.java:3207)  at com.pegarules.generated.html_harness.ra_stream_harnessfail_cc54e2e00e5fa3171adb4ff3b8301fe0.generatePegaHarnessDiv_6(ra_stream_harnessfail_cc54e2e00e5fa3171adb4ff3b8301fe0.java:2019)  at com.pegarules.generated.html_harness.ra_stream_harnessfail_cc54e2e00e5fa3171adb4ff3b8301fe0.execute(ra_stream_harnessfail_cc54e2e00e5fa3171adb4ff3b8301fe0.java:672)  at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:4033)  at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:3861)  at com.pegarules.generated.activity.ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.step6_circum0(ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.java:731)  at com.pegarules.generated.activity.ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.perform(ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.java:155)  at com.pega.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3500)  at com.pega.pegarules.session.internal.mgmt.Executable.invokeActivity(Executable.java:10514)  at com.pegarules.generated.activity.ra_action_activitystatusnocontenthandler_ff36b7afe477ab0eedd010dc329bf2bb.step1_circum0(ra_action_activitystatusnocontenthandler_ff36b7afe477ab0eedd010dc329bf2bb.java:173)  at com.pegarules.generated.activity.ra_action_activitystatusnocontenthandler_ff36b7afe477ab0eedd010dc329bf2bb.perform(ra_action_activitystatusnocontenthandler_ff36b7afe477ab0eedd010dc329bf2bb.java:69)  at com.pega.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3500)  at com.pega.pegarules.session.internal.mgmt.base.ThreadRunner.runActivitiesAlt(ThreadRunner.java:646)  at com.pega.pegarules.session.internal.mgmt.PRThreadImpl.runActivitiesAlt(PRThreadImpl.java:461)  at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.runActivities(HttpAPI.java:3322)  at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.postProcessContent(HttpAPI.java:3716)  at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.activityExecutionEpilog(EngineAPI.java:570)  at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRequestInner(EngineAPI.java:459)  at sun.reflect.GeneratedMethodAccessor68.invoke(Unknown Source)  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)  at java.lang.reflect.Method.invoke(Method.java:606)  at com.pega.pegarules.session.internal.PRSessionProviderImpl.performTargetActionWithLock(PRSessionProviderImpl.java:1270)  at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:1008)  at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:841)  at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRequest(EngineAPI.java:331)  at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.invoke(HttpAPI.java:850)  at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl._invokeEngine_privact(EngineImpl.java:315)  at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:263)  at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:240)  at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngineInner(JNDIEnvironment.java:278)  at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngine(JNDIEnvironment.java:223)  at com.pega.pegarules.web.impl.WebStandardImpl.makeEtierRequest(WebStandardImpl.java:574)  at com.pega.pegarules.web.impl.WebStandardImpl.doPost(WebStandardImpl.java:374)  at sun.reflect.GeneratedMethodAccessor65.invoke(Unknown Source)  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)  at java.lang.reflect.Method.invoke(Method.java:606)  at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethod(PRBootstrap.java:367)  at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethodPropagatingThrowable(PRBootstrap.java:408)  at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethodPropagatingThrowable(AppServerBridgeToPega.java:223)  at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethod(AppServerBridgeToPega.java:272)  at com.pega.pegarules.internal.web.servlet.WebStandardBoot.doPost(WebStandardBoot.java:121)  at javax.servlet.http.HttpServlet.service(HttpServlet.java:754)  at javax.servlet.http.HttpServlet.service(HttpServlet.java:847)  at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:295)  at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214)  at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:231)  at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:149)  at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:420)  at org.jboss.as.web.session.ClusteredSessionValve.handleRequest(ClusteredSessionValve.java:134)  at org.jboss.as.web.session.ClusteredSessionValve.invoke(ClusteredSessionValve.java:99)  at org.jboss.as.web.session.JvmRouteValve.invoke(JvmRouteValve.java:92)  at org.jboss.as.web.session.LockingValve.invoke(LockingValve.java:64)  at org.jboss.as.web.security.SecurityContextAssociationValve.invoke(SecurityContextAssociationValve.java:169)  at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:145)  at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:97)  at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:102)  at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)  at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:856)  at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:653)  at org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:926)  at java.lang.Thread.run(Thread.java:745) ]";
		String message3 = "[MSG][Problem during PRException.getRuleMessage: \"FUACache-FailAssembly [this:is:not:a:classname] produced no java source\"][STACK][java.util.NoSuchElementException   at java.util.LinkedList.getLast(LinkedList.java:257)   at com.pega.pegarules.monitor.internal.database.ReporterStackImpl.pop(ReporterStackImpl.java:130)   at com.pega.pegarules.data.internal.access.DatabaseImpl.open(DatabaseImpl.java:4205)   at com.pega.pegarules.data.internal.access.DatabaseImpl.open(DatabaseImpl.java:3800)   at com.pega.pegarules.data.internal.access.DatabaseImpl.open(DatabaseImpl.java:3768)   at com.pega.pegarules.session.internal.mgmt.MessageEvaluator.getRuleMessage(MessageEvaluator.java:437)   at com.pega.pegarules.session.internal.mgmt.MessageEvaluator.getRuleMessage(MessageEvaluator.java:373)   at com.pega.pegarules.session.internal.mgmt.MessageEvaluator.getRuleMessage(MessageEvaluator.java:324)   at com.pega.pegarules.session.internal.mgmt.MessageEvaluator.getRuleMessage(MessageEvaluator.java:317)   at com.pega.pegarules.session.internal.PRSessionProviderImpl.getRuleMessage(PRSessionProviderImpl.java:1775)   at com.pega.pegarules.session.internal.mgmt.Executable.getRuleMessage(Executable.java:6988)   at com.pega.pegarules.pub.PRException.getRuleMessage(PRException.java:385)   at com.pega.pegarules.pub.PRException. (PRException.java:131)   at com.pega.pegarules.pub.generator.FirstUseAssemblerException. (FirstUseAssemblerException.java:55)   at com.pega.pegarules.generation.internal.cache.AssemblerFunctions.assembleRule(AssemblerFunctions.java:179)   at com.pega.pegarules.generation.internal.cache.AssemblyCacheBase.buildAndOrLoadJavaClass(AssemblyCacheBase.java:1675)   at com.pega.pegarules.generation.internal.cache.AssemblyCacheBase.getGeneratedJava(AssemblyCacheBase.java:3091)   at com.pega.pegarules.generation.internal.cache.appcentric.RACacheAppCentricImpl.addEntryToMemoryCache(RACacheAppCentricImpl.java:1161)   at com.pega.pegarules.generation.internal.cache.appcentric.RACacheAppCentricImpl.find(RACacheAppCentricImpl.java:1008)   at com.pega.pegarules.generation.internal.cache.AssemblyCacheWrapper.find(AssemblyCacheWrapper.java:799)   at com.pega.pegarules.generation.internal.assembly.FUAManagerImpl.getInternal(FUAManagerImpl.java:1408)   at com.pega.pegarules.generation.internal.assembly.FUAManagerImpl.get(FUAManagerImpl.java:1296)   at com.pega.pegarules.generation.internal.PRGenProviderImpl.get(PRGenProviderImpl.java:476)   at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:3992)   at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:3866)   at com.pegarules.generated.activity.ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.step6_circum0(ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.java:684)   at com.pegarules.generated.activity.ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.perform(ra_action_show_harness_3a715fceb3725b54f857d8327a1c5bba.java:155)   at com.pega.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3505)   at com.pega.pegarules.session.internal.mgmt.Executable.invokeActivity(Executable.java:10563)   at com.pegarules.generated.activity.ra_action_activitystatusexceptionhandler_5e1c79001fa282ce50aa911a3a1e736f.step3_circum0(ra_action_activitystatusexceptionhandler_5e1c79001fa282ce50aa911a3a1e736f.java:326)   at com.pegarules.generated.activity.ra_action_activitystatusexceptionhandler_5e1c79001fa282ce50aa911a3a1e736f.perform(ra_action_activitystatusexceptionhandler_5e1c79001fa282ce50aa911a3a1e736f.java:112)   at com.pega.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3505)   at com.pega.pegarules.session.internal.mgmt.base.ThreadRunner.runActivitiesAlt(ThreadRunner.java:646)   at com.pega.pegarules.session.internal.mgmt.PRThreadImpl.runActivitiesAlt(PRThreadImpl.java:461)   at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.runActivities(HttpAPI.java:3358)   at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.postProcessContent(HttpAPI.java:3754)   at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.activityExecutionEpilog(EngineAPI.java:570)   at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRequestInner(EngineAPI.java:459)   at sun.reflect.GeneratedMethodAccessor44.invoke(Unknown Source)   at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)   at java.lang.reflect.Method.invoke(Method.java:497)   at com.pega.pegarules.session.internal.PRSessionProviderImpl.performTargetActionWithLock(PRSessionProviderImpl.java:1270)   at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:1008)   at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:841)   at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRequest(EngineAPI.java:331)   at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.invoke(HttpAPI.java:852)   at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl._invokeEngine_privact(EngineImpl.java:315)   at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:263)   at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:240)   at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngineInner(JNDIEnvironment.java:278)   at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngine(JNDIEnvironment.java:223)   at com.pega.pegarules.web.impl.WebStandardImpl.makeEtierRequest(WebStandardImpl.java:574)   at com.pega.pegarules.web.impl.WebStandardImpl.doPost(WebStandardImpl.java:374)   at sun.reflect.GeneratedMethodAccessor43.invoke(Unknown Source)   at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)   at java.lang.reflect.Method.invoke(Method.java:497)   at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethod(PRBootstrap.java:370)   at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethodPropagatingThrowable(PRBootstrap.java:411)   at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethodPropagatingThrowable(AppServerBridgeToPega.java:223)   at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethod(AppServerBridgeToPega.java:272)   at com.pega.pegarules.internal.web.servlet.WebStandardBoot.doPost(WebStandardBoot.java:121)   at javax.servlet.http.HttpServlet.service(HttpServlet.java:754)   at javax.servlet.http.HttpServlet.service(HttpServlet.java:847)   at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:295)   at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214)   at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:231)   at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:149)   at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:420)   at org.jboss.as.web.session.ClusteredSessionValve.handleRequest(ClusteredSessionValve.java:134)   at org.jboss.as.web.session.ClusteredSessionValve.invoke(ClusteredSessionValve.java:99)   at org.jboss.as.web.session.JvmRouteValve.invoke(JvmRouteValve.java:92)   at org.jboss.as.web.session.LockingValve.invoke(LockingValve.java:64)   at org.jboss.as.web.security.SecurityContextAssociationValve.invoke(SecurityContextAssociationValve.java:169)   at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:150)   at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:97)   at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:102)   at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)   at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:854)   at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:653)   at org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:926)   at java.lang.Thread.run(Thread.java:745)  ]";
		String message4 = "[MSG][Section 'InvestementReasons' execution error on page 'tempPage' of class 'MEB-FW-OBFW-Data-Product'.][STACK][java.lang.NullPointerException  ]";
		String message5 = "[MSG][Section 'pzGridModalHTML' execution error on page 'pyWorkPage' of  class ''.][STACK][java.lang.NullPointerException<CR><CR>	at com.pegarules.generated.html.ra_stream_pzgridmodalhtml_87eccc7bbbfb6ad23b0144a6334e857d.execute(ra_stream_pzgridmodalhtml_87eccc7bbbfb6ad23b0144a6334e857d.java:827)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:4038)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:3866)<CR><CR>	at com.pegarules.generated.html.ra_stream_pyshowstream_0ac1b9f64c756da2a320f899d95ff35f.execute(ra_stream_pyshowstream_0ac1b9f64c756da2a320f899d95ff35f.java:129)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:4038)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.Executable.getStream(Executable.java:3866)<CR><CR>	at com.pega.pegarules.pub.runtime.AbstractActivity.showHtml(AbstractActivity.java:247)<CR><CR>	at com.pegarules.generated.activity.ra_action_showstream_f351378ddef289e737ba4d8fe14e1953.step6_circum0(ra_action_showstream_f351378ddef289e737ba4d8fe14e1953.java:160)<CR><CR>	at com.pegarules.generated.activity.ra_action_showstream_f351378ddef289e737ba4d8fe14e1953.perform(ra_action_showstream_f351378ddef289e737ba4d8fe14e1953.java:69)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.Executable.doActivity(Executable.java:3505)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.base.ThreadRunner.runActivitiesAlt(ThreadRunner.java:646)<CR><CR>	at com.pega.pegarules.session.internal.mgmt.PRThreadImpl.runActivitiesAlt(PRThreadImpl.java:461)<CR><CR>	at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.runActivities(HttpAPI.java:3358)<CR><CR>	at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRequestInner(EngineAPI.java:385)<CR><CR>	at sun.reflect.GeneratedMethodAccessor44.invoke(Unknown Source)<CR><CR>	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)<CR><CR>	at java.lang.reflect.Method.invoke(Method.java:497)<CR><CR>	at com.pega.pegarules.session.internal.PRSessionProviderImpl.performTargetActionWithLock(PRSessionProviderImpl.java:1270)<CR><CR>	at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:1008)<CR><CR>	at com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:841)<CR><CR>	at com.pega.pegarules.session.external.engineinterface.service.EngineAPI.processRequest(EngineAPI.java:331)<CR><CR>	at com.pega.pegarules.session.internal.engineinterface.service.HttpAPI.invoke(HttpAPI.java:852)<CR><CR>	at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl._invokeEngine_privact(EngineImpl.java:315)<CR><CR>	at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:263)<CR><CR>	at com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:240)<CR><CR>	at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngineInner(JNDIEnvironment.java:278)<CR><CR>	at com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngine(JNDIEnvironment.java:223)<CR><CR>	at com.pega.pegarules.web.impl.WebStandardImpl.makeEtierRequest(WebStandardImpl.java:574)<CR><CR>	at com.pega.pegarules.web.impl.WebStandardImpl.doPost(WebStandardImpl.java:374)<CR><CR>	at sun.reflect.GeneratedMethodAccessor43.invoke(Unknown Source)<CR><CR>	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)<CR><CR>	at java.lang.reflect.Method.invoke(Method.java:497)<CR><CR>	at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethod(PRBootstrap.java:370)<CR><CR>	at com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethodPropagatingThrowable(PRBootstrap.java:411)<CR><CR>	at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethodPropagatingThrowable(AppServerBridgeToPega.java:223)<CR><CR>	at com.pega.pegarules.boot.internal.extbridge.AppServerBridgeToPega.invokeMethod(AppServerBridgeToPega.java:272)<CR><CR>	at com.pega.pegarules.internal.web.servlet.WebStandardBoot.doPost(WebStandardBoot.java:121)<CR><CR>	at javax.servlet.http.HttpServlet.service(HttpServlet.java:754)<CR><CR>	at javax.servlet.http.HttpServlet.service(HttpServlet.java:847)<CR><CR>	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:295)<CR><CR>	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214)<CR><CR>	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:231)<CR><CR>	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:149)<CR><CR>	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:420)<CR><CR>	at org.jboss.as.web.session.ClusteredSessionValve.handleRequest(ClusteredSessionValve.java:134)<CR><CR>	at org.jboss.as.web.session.ClusteredSessionValve.invoke(ClusteredSessionValve.java:99)<CR><CR>	at org.jboss.as.web.session.JvmRouteValve.invoke(JvmRouteValve.java:92)<CR><CR>	at org.jboss.as.web.session.LockingValve.invoke(LockingValve.java:64)<CR><CR>	at org.jboss.as.web.security.SecurityContextAssociationValve.invoke(SecurityContextAssociationValve.java:169)<CR><CR>	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:150)<CR><CR>	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:97)<CR><CR>	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:102)<CR><CR>	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)<CR><CR>	at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:854)<CR><CR>	at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:653)<CR><CR>	at org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:926)<CR><CR>	at java.lang.Thread.run(Thread.java:745)<CR><CR>]";

		String regex = "\\]\\[STACK\\]\\[(.*)\\]";
		Pattern pattern = Pattern.compile(regex);

		String exceptionRegex = "([\\w\\.]*(Exception|Error))[\\s:<;]";
		Pattern exceptionPattern = Pattern.compile(exceptionRegex);

		Matcher patternMatcher = pattern.matcher(message);
		boolean matches = patternMatcher.find();
		// System.out.println(matches);

		if (matches) {

			String stack = patternMatcher.group(1);
			// System.out.println(stack);

			Matcher exceptionPatternMatcher = exceptionPattern.matcher(stack);
			matches = exceptionPatternMatcher.find();
			System.out.println(matches);

			if (matches) {
				String exception = exceptionPatternMatcher.group(1);
				System.out.println(exception);
			} else if (stack.startsWith("java.lang.Throwable")) {
				System.out.println("java.lang.Throwable");
			}

		}
		long after = System.currentTimeMillis();

		System.out.println(after - before);
	}

}
