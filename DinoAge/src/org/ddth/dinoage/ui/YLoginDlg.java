/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 11:48:11 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.dinoage.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.ddth.dinoage.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mozilla.interfaces.nsICookie;
import org.mozilla.interfaces.nsICookieManager;
import org.mozilla.interfaces.nsIServiceManager;
import org.mozilla.interfaces.nsISimpleEnumerator;
import org.mozilla.xpcom.Mozilla;

public class YLoginDlg {
	
	private Shell shell;
	private boolean isLogged = false;
	private CookieStore cookieStore = new BasicCookieStore();

	public CookieStore getCookieStore() {
		return cookieStore;		
	}
	
	private boolean createCookieStore() {
		nsIServiceManager serviceManager = Mozilla.getInstance().getServiceManager();
		nsICookieManager cookieManager = (nsICookieManager) serviceManager.getServiceByContractID(
				"@mozilla.org/cookiemanager;1", nsICookieManager.NS_ICOOKIEMANAGER_IID);
		nsISimpleEnumerator cookieEnumerator = cookieManager.getEnumerator();

		int sessionCookie = 0;
		cookieStore.clear();
		while (cookieEnumerator.hasMoreElements()) {
			nsICookie cookie = (nsICookie) cookieEnumerator.getNext().queryInterface(nsICookie.NS_ICOOKIE_IID);
			BasicClientCookie basicClientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
			basicClientCookie.setDomain(cookie.getHost());
			basicClientCookie.setPath(cookie.getPath());
			basicClientCookie.setSecure(cookie.getIsSecure());
			if (cookie.getExpires() == 0) {
				// Session cookie... => Add 1 more year to be expired ;))
				basicClientCookie.setExpiryDate(new Date(System.currentTimeMillis() + 31536000000L));
				sessionCookie++;
			}
			else {
				basicClientCookie.setExpiryDate(new Date((long)cookie.getExpires() * 1000));
			}
			cookieStore.addCookie(basicClientCookie);
		}
		return sessionCookie > 1;
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public YLoginDlg() {
		System.setProperty("org.eclipse.swt.browser.XULRunnerPath", new File("xulrunner").getAbsolutePath());
	}

	/**
	 * Open the dialog
	 */
	public void open() {
		createContents();

		shell.setText(ResourceManager.getMessage(ResourceManager.KEY_LOGIN_DIALOG_TITLE));
		shell.pack();
		shell.setSize(220, 150);
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent event) {
				if (!isLogged) {
					UniversalUtil.showMessageBox(shell, shell.getText(),
							ResourceManager.getMessage(ResourceManager.KEY_LOGIN_FAILED_MESSAGE,
									new String[] {ResourceManager.KEY_PRODUCT_NAME}));
				}
				event.doit = true;
			}
		});
		
		// Center the shell
		UniversalUtil.centerWindow(shell);

		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private String getLoginFormHTML() {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("loginForm.html")));
		StringBuffer sBuffer = new StringBuffer();
		try {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				sBuffer.append(line);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return sBuffer.toString();
	}
	
	/**
	 * Create contents of the dialog
	 */
	protected void createContents() {

		shell = new Shell(Display.getDefault(), SWT.CENTER | SWT.CLOSE);
		shell.setLayout(new FillLayout());

		try {
			final Browser browser = new Browser(shell, SWT.MOZILLA);
			browser.setText(getLoginFormHTML());
			browser.addProgressListener(new ProgressListener() {
				boolean isCompleted = false;
				public void changed(ProgressEvent event) {
					if (isCompleted) {
						browser.stop();
						isLogged = createCookieStore();
						shell.close();
					}
				}

				public void completed(ProgressEvent event) {
					isCompleted = true;
				}
			});
		}
		catch (SWTError e) {
			e.printStackTrace();
		}
	}
}
