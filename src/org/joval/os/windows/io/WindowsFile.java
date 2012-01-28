// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package org.joval.os.windows.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;

import org.joval.intf.io.IFile;
import org.joval.intf.io.IRandomAccess;
import org.joval.intf.util.tree.INode;
import org.joval.intf.windows.identity.IACE;
import org.joval.intf.windows.io.IWindowsFile;
import org.joval.os.windows.identity.LocalACE;

/**
 * Defines extended attributes of a file on Windows.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class WindowsFile implements IWindowsFile {
    private IFile f;

    public WindowsFile(IFile f) {
	this.f = f;
    }

    // Implement INode

    public Collection<INode> getChildren() throws NoSuchElementException, UnsupportedOperationException {
	return f.getChildren();
    }

    public Collection<INode> getChildren(Pattern p) throws NoSuchElementException, UnsupportedOperationException {
	return f.getChildren(p);
    }

    public INode getChild(String name) throws NoSuchElementException, UnsupportedOperationException {
	return f.getChild(name);
    }

    public String getName() {
	return f.getName();
    }

    public String getPath() {
	return f.getPath();
    }

    public String getCanonicalPath() {
	return f.getCanonicalPath();
    }

    public Type getType() {
	return f.getType();
    }

    public boolean hasChildren() throws NoSuchElementException {
	return f.hasChildren();
    }

    // Implement IFile

    public long accessTime() throws IOException {
	return f.accessTime();
    }

    public long createTime() throws IOException {
	return f.createTime();
    }

    public boolean exists() throws IOException {
	return f.exists();
    }

    public boolean mkdir() {
	return f.mkdir();
    }

    public InputStream getInputStream() throws IOException {
	return f.getInputStream();
    }

    public OutputStream getOutputStream(boolean append) throws IOException {
	return f.getOutputStream(append);
    }

    public IRandomAccess getRandomAccess(String mode) throws IllegalArgumentException, IOException {
	return f.getRandomAccess(mode);
    }

    public boolean isDirectory() throws IOException {
	return f.isDirectory();
    }

    public boolean isFile() throws IOException {
	return f.isFile();
    }

    public boolean isLink() throws IOException {
	return f.isLink();
    }

    public long lastModified() throws IOException {
	return f.lastModified();
    }

    public long length() throws IOException {
	return f.length();
    }

    public String[] list() throws IOException {
	return f.list();
    }

    public IFile[] listFiles() throws IOException {
	ArrayList<IFile> list = new ArrayList<IFile>();
	for (INode node : f.getChildren()) {
	    list.add((IFile)node);
	}
	return list.toArray(new IFile[list.size()]);
    }

    public void delete() throws IOException {
	f.delete();
    }

    public String getLocalName() {
	return f.getLocalName();
    }

    public String toString() {
	return f.toString();
    }

    // Implement IWindowsFile

    /**
     * Returns one of the FILE_TYPE_ constants.
     */
    public int getWindowsFileType() throws IOException {
	try {
	    int dirAttr = FILE_ATTRIBUTE_DIRECTORY & Kernel32Util.getFileAttributes(getPath());
	    if (FILE_ATTRIBUTE_DIRECTORY == dirAttr) {
		return FILE_ATTRIBUTE_DIRECTORY;
	    } else {
		return Kernel32Util.getFileType(getLocalName());
	    }
	} catch (Win32Exception e) {
	    throw new IOException(e);
	}
    }

    public IACE[] getSecurity() throws IOException {
	try {
	    WinNT.ACCESS_ACEStructure[] aces = Advapi32Util.getFileSecurity(getLocalName(), false);
	    IACE[] result = new IACE[aces.length];
	    for (int i=0; i < aces.length; i++) {
		result[i] = new LocalACE(aces[i]);
	    }
	    return result;
	} catch (Win32Exception e) {
	    throw new IOException(e);
	}
    }
}
