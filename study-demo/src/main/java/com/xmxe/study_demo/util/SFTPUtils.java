package com.xmxe.study_demo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;


/**
* @ClassName: SFTPUtils
* @Description: sftp工具类
*/
public class SFTPUtils {
	private static Logger log = Logger.getLogger(SFTPUtils.class.getName());

	private String masterhost;// 服务器连接ip
	private String bakhost;// 服务器连接备用ip
	private String username;// 用户名
	private String password;// 密码
	private int port = 22;// 端口号
	private ChannelSftp sftp = null;
	private Session sshSession = null;

	public SFTPUtils() {
	}

	public SFTPUtils(String masterhost,String bakHost, int port, String username, String password) {
		this.masterhost = masterhost;
		this.bakhost = bakHost;
		this.username = username;
		this.password = password;
		this.port = port;
	}

	public SFTPUtils(String masterhost,String bakHost, String username, String password) {
		this.masterhost = masterhost;
		this.bakhost = bakHost;
		this.username = username;
		this.password = password;
	}

	/**
	 * 通过SFTP连接服务器
	 */
	public void connect() {
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, masterhost, port);
			sshSession = jsch.getSession(username, masterhost, port);
			 
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			 
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			 
			sftp = (ChannelSftp) channel;
			 
		} catch (Exception e) {
			try {
				log.warn("与SFTP服务器断开，启用备用服务器---------");
				JSch jsch = new JSch();
				jsch.getSession(username, bakhost, port);
				sshSession = jsch.getSession(username, bakhost, port);
				 
				sshSession.setPassword(password);
				Properties sshConfig = new Properties();
				sshConfig.put("StrictHostKeyChecking", "no");
				sshSession.setConfig(sshConfig);
				sshSession.connect();
				 
				Channel channel = sshSession.openChannel("sftp");
				channel.connect();
				 
				sftp = (ChannelSftp) channel;
			} catch (Exception e2) {
				log.error("连接SFTP异常：",e2);
			}
			
		}
	}

	/**
	 * 关闭连接
	 */
	public void disconnect() {
		if (this.sftp != null) {
			if (this.sftp.isConnected()) {
				this.sftp.disconnect();
			}
		}
		if (this.sshSession != null) {
			if (this.sshSession.isConnected()) {
				this.sshSession.disconnect();
			}
		}
	}

	/**
	 * 批量下载文件
	 * 
	 * @param remotPath
	 *            ：远程下载目录(以路径符号结束,可以为相对路径eg:/assess/sftp/jiesuan_2/2014/)
	 * @param localPath
	 *            ：本地保存目录(以路径符号结束,D:\Duansha\sftp\)
	 * @param fileFormat
	 *            ：下载文件格式(以特定字符开头,为空不做检验)
	 * @param fileEndFormat
	 *            ：下载文件格式(文件格式)
	 * @param del
	 *            ：下载后是否删除sftp文件
	 * @return
	 */
	public List<String> batchDownLoadFile(String remotePath, String localPath,
			String fileFormat, String fileEndFormat, boolean del) {
		List<String> filenames = new ArrayList<String>();
		try {
			// connect();
			Vector v = listFiles(remotePath);
			// sftp.cd(remotePath);
			if (v.size() > 0) {
				log.info("本次处理文件个数不为零,开始下载...fileSize=" + v.size());
				Iterator it = v.iterator();
				while (it.hasNext()) {
					LsEntry entry = (LsEntry) it.next();
					String filename = entry.getFilename();
					SftpATTRS attrs = entry.getAttrs();
					if (!attrs.isDir()) {
						boolean flag = false;
						String localFileName = localPath + filename;
						fileFormat = fileFormat == null ? "" : fileFormat
								.trim();
						fileEndFormat = fileEndFormat == null ? ""
								: fileEndFormat.trim();
						// 三种情况
						if (fileFormat.length() > 0
								&& fileEndFormat.length() > 0) {
							if (filename.startsWith(fileFormat)
									&& filename.endsWith(fileEndFormat)) {
								flag = downloadFile(remotePath, filename,
										localPath, filename);
								if (flag) {
									filenames.add(localFileName);
									if (flag && del) {
										deleteSFTP(remotePath, filename);
									}
								}
							}
						} else if (fileFormat.length() > 0
								&& "".equals(fileEndFormat)) {
							if (filename.startsWith(fileFormat)) {
								flag = downloadFile(remotePath, filename,
										localPath, filename);
								if (flag) {
									filenames.add(localFileName);
									if (flag && del) {
										deleteSFTP(remotePath, filename);
									}
								}
							}
						} else if (fileEndFormat.length() > 0
								&& "".equals(fileFormat)) {
							if (filename.endsWith(fileEndFormat)) {
								flag = downloadFile(remotePath, filename,
										localPath, filename);
								if (flag) {
									filenames.add(localFileName);
									if (flag && del) {
										deleteSFTP(remotePath, filename);
									}
								}
							}
						} else {
							flag = downloadFile(remotePath, filename,
									localPath, filename);
							if (flag) {
								filenames.add(localFileName);
								if (flag && del) {
									deleteSFTP(remotePath, filename);
								}
							}
						}
					}
				}
			}
			if (log.isInfoEnabled()) {
				log.info("download file is success:remotePath=" + remotePath
						+ "and localPath=" + localPath + ",file size is"
						+ v.size());
			}
		} catch (SftpException e) {
			log.error(e);
		} finally {
			// this.disconnect();
		}
		return filenames;
	}
	/**
	 * 获取目录下的文件名集合
	 * 
	 * @param remotPath
	 *            ：远程文件目录(以路径符号结束,可以为相对路径eg:/assess/sftp/jiesuan_2/2014/)
	 * @return
	 */
	public List<String> getFiles(String remotePath,String fromTime) {
		List<String> filenames = new ArrayList<String>();
		try {
			Vector v = listFiles(remotePath);
			if (v.size() > 0) {
				Iterator<LsEntry> it = v.iterator();
				while (it.hasNext()) {
					LsEntry entry = (LsEntry) it.next();
					String filename = entry.getFilename();
					if(!fromTime(filename,fromTime)) continue;
					
					SftpATTRS attrs = entry.getAttrs();
					if (!attrs.isDir()) {
//						String localFileName = localPath + filename;
						filenames.add(filename);
					}
				}
				
			}
			 
		} catch (SftpException e) {
			log.error("获取目录下的文件名集合异常",e);
		} finally {
			// this.disconnect();
		}
		return  filenames;
	}
	
	public boolean fromTime(String filename,String fromTime){		
		try{
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
			String time = filename.substring(7,21);
			return sf.parse(time).getTime() > sf.parse(fromTime).getTime();
		}catch(Exception e){			
			
		}
		return true;
	}
	/**
	 * 批量下载文件
	 * 
	 * @param remotPath
	 *            ：远程下载目录(以路径符号结束)
	 * @param localPath
	 *            ：本地保存目录(以路径符号结束)
	 * @param fileNames
	 *            ：文件名集合
	 * @return
	 */
	public List<String> batchDownloadFile(String remotePath,String localPath,List<String> fileNames){
		List<String> localFileList = new ArrayList<>();
		for(String fileName:fileNames){
			downloadFile(remotePath,fileName,localPath,fileName);
			localFileList.add(localPath+fileName);
		}
		return localFileList;
	}
	/**
	 * 下载单个文件
	 * 
	 * @param remotPath
	 *            ：远程下载目录(以路径符号结束)
	 * @param remoteFileName
	 *            ：下载文件名
	 * @param localPath
	 *            ：本地保存目录(以路径符号结束)
	 * @param localFileName
	 *            ：保存文件名
	 * @return
	 */
	public boolean downloadFile(String remotePath, String remoteFileName,
			String localPath, String localFileName) {
		FileOutputStream fieloutput = null;
		try {
			File localPaths = new File(localPath);
			if(!localPaths.exists()){
				localPaths.mkdirs();
			}
			File file = new File(localPath + localFileName);
//			mkdirs(localPath + localFileName);
			fieloutput = new FileOutputStream(file);
			sftp.get(remotePath + remoteFileName, fieloutput);
			 
			return true;
		} catch (FileNotFoundException e) {
			log.error("下载文件异常：",e);
		} catch (SftpException e) {
			log.error("下载文件异常：",e);
		} finally {
			if (null != fieloutput) {
				try {
					fieloutput.close();
				} catch (IOException e) {
					log.error("",e);
				}
			}
		}
		return false;
	}

	/**
	 * 上传单个文件
	 * 
	 * @param remotePath
	 *            ：远程保存目录
	 * @param remoteFileName
	 *            ：保存文件名
	 * @param localFilePath
	 *            ：本地上传的文件 完整路径
	 * @return
	 */
	public boolean uploadFile(String remotePath, String remoteFileName,
			String localFilePath) {
		FileInputStream in = null;
		try {
			createDir(remotePath);
			File file = new File(localFilePath);
			in = new FileInputStream(file);
			sftp.put(in, remoteFileName);
			return true;
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (SftpException e) {
			log.error(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
		return false;
	}

	/**
	 * 批量上传文件
	 * 
	 * @param remotePath
	 *            ：远程保存目录
	 * @param localPath
	 *            ：本地上传目录(以路径符号结束)
	 * @param del
	 *            ：上传后是否删除本地文件
	 * @return
	 */
	public boolean bacthUploadFile(String remotePath, String localPath,
			boolean del) {
		try {
			connect();
			File file = new File(localPath);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()
						&& files[i].getName().indexOf("bak") == -1) {
					if (this.uploadFile(remotePath, files[i].getName(),
							localPath+files[i].getName()) && del) {
						deleteFile(localPath + files[i].getName());
					}
				}
			}
			if (log.isInfoEnabled()) {
				log.info("upload file is success:remotePath=" + remotePath
						+ "and localPath=" + localPath + ",file size is "
						+ files.length);
			}
			return true;
		} catch (Exception e) {
			log.error(e);
		} finally {
			this.disconnect();
		}
		return false;

	}

	/**
	 * 删除本地文件
	 * 
	 * @param filePath
	 * @return
	 */
	public boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return false;
		}

		if (!file.isFile()) {
			return false;
		}
		boolean rs = file.delete();
		if (rs && log.isInfoEnabled()) {
			log.info("delete file success from local.");
		}
		return rs;
	}

	/**
	 * 创建目录
	 * 
	 * @param createpath
	 * @return
	 */
	public boolean createDir(String createpath) {
		try {
			if (isDirExist(createpath)) {
				this.sftp.cd(createpath);
				return true;
			}
			String pathArry[] = createpath.split("/");
			StringBuffer filePath = new StringBuffer("/");
			for (String path : pathArry) {
				if (path.equals("")) {
					continue;
				}
				filePath.append(path + "/");
				if (isDirExist(filePath.toString())) {
					sftp.cd(filePath.toString());
				} else {
					// 建立目录
					sftp.mkdir(filePath.toString());
					// 进入并设置为当前目录
					sftp.cd(filePath.toString());
				}

			}
			this.sftp.cd(createpath);
			return true;
		} catch (SftpException e) {
			log.error(e);
		}
		return false;
	}

	/**
	 * 判断目录是否存在
	 * 
	 * @param directory
	 * @return
	 */
	public boolean isDirExist(String directory) {
		boolean isDirExistFlag = false;
		try {
			SftpATTRS sftpATTRS = sftp.lstat(directory);
			isDirExistFlag = true;
			return sftpATTRS.isDir();
		} catch (Exception e) {
			if (e.getMessage().toLowerCase().equals("no such file")) {
				isDirExistFlag = false;
			}
		}
		return isDirExistFlag;
	}

	/**
	 * 删除stfp文件
	 * 
	 * @param directory
	 *            ：要删除文件所在目录
	 * @param deleteFile
	 *            ：要删除的文件
	 * @param sftp
	 */
	public void deleteSFTP(String directory, String deleteFile) {
		try {
			// sftp.cd(directory);
			sftp.rm(directory + deleteFile);
			if (log.isInfoEnabled()) {
				log.info("delete file success from sftp.");
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * 如果目录不存在就创建目录
	 * 
	 * @param path
	 */
	public void mkdirs(String path) {
		File f = new File(path);

		String fs = f.getParent();

		f = new File(fs);

		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory
	 *            ：要列出的目录
	 * @param sftp
	 * @return
	 * @throws SftpException
	 */
	public Vector listFiles(String directory) throws SftpException {
		return sftp.ls(directory);
	}
	
	public void cd(String directory) throws SftpException {
		sftp.cd(directory);
	}
	/**
	 * 获取当前目录
	 * 
	 * @return
	 * @throws SftpException
	 */
	public String pwd() throws SftpException {
		return sftp.pwd();
	}

	public String getHost() {
		return masterhost;
	}

	public void setHost(String host) {
		this.masterhost = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ChannelSftp getSftp() {
		return sftp;
	}

	public void setSftp(ChannelSftp sftp) {
		this.sftp = sftp;
	}


	/**
	 * @Description 下载附件
	 * @param args
	 */
	public static void downLoadFile() {
//		
//		// 获取FTP的配置项  待配置
//		String ip = ConfigManageUtil.firstLevle_ftp_ip;
//		String port = ConfigManageUtil.firstLevle_ftp_port;
//		String username = ConfigManageUtil.firstLevle_ftp_userword;
//		String password = ConfigManageUtil.firstLevle_ftp_password;
//		String directory = ConfigManageUtil.firstLevle_uploadbin;
//		// 获取保存文件的配置路径
////	    String localPath = ConfigManageUtil.firstLevle_ftp_localPath;
//		SFTPUtils sftp = null;
//		// 本地存放地址
//		String localPath = ConfigManageUtil.firstLevle_ftp_localPath;;
//		// Sftp下载路径
//		String sftpPath = ConfigManageUtil.firstLevle_uploadbin;
//		List<String> filePathList = new ArrayList<String>();
//		try {
//			sftp = new SFTPUtils(ip,Integer.parseInt(port),username, password);
//			sftp.connect();
//			// 下载
//			sftp.batchDownLoadFile(sftpPath, localPath, "attach_", "", true);
//		} catch (Exception e) {
//			log.error(e);
//			
//		} finally {
//			sftp.disconnect();
//		}
	}
	
	public static void main(String[] args) {
		SFTPUtils sftp = null;
		// 本地存放地址
		String localPath = "D://";
		String host = "127.0.0.1";
		int port = 22;
		String username="fh";
		String password="fh.3798873";
		// Sftp下载路径
		String sftpPath = "/home/fh/";
		
		try {
			sftp = new SFTPUtils(host,host,port,username, password);
			sftp.connect();
			sftp.sftp.cd("sftptest");
			System.out.println(sftp.pwd()+"/");;
			// 下载
//			sftp.batchDownLoadFile(sftpPath, localPath, "", "txt", false);
			List<String> list = sftp.getFiles(sftp.pwd(),"20200521133000");
			List<String> localList =  sftp.batchDownloadFile(sftp.pwd()+"/",localPath,list);
			for(String s:localList){
				System.out.println(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			
		} finally {
			sftp.disconnect();
		}
	}

}
