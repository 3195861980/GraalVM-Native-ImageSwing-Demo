## 前提条件

<br/>

1. 安装[ Liberica Native Image Kit](https://bell-sw.com/pages/downloads/native-image-kit/#nik-23-(jdk-17)) 版本我选择的是==17.0.11+10-23.0.4+1==

2. 配置JDK环境变量

3. 安装 visual studio [地址直达](https://visualstudio.microsoft.com/zh-hans/)  下载完成选择 c++桌面开发进行安装
   
   <br/>

## 开始打包镜像

1. 提前把项目打包成==jar包==  确保 java -jar [jar name] 可以正常运行（不能运行直接不能进行下一步）

2. 管理员权限打开 **x64 Native Tools Command Prompt for VS 2022x64 Native Tools Command Prompt for VS 2022** 

3. 进入 jar包的位置

4. 打包镜像
   
   ```sh
   native-image -Djava.awt.headless=false -jar emailSender-1.0-SNAPSHOT.jar --no-fallback -H:ReflectionConfigurationFiles=reflect-config.json -H:Path=out --enable-url-protocols=https
   ```

#### 命令解释

1. -Djava.awt.headless=false：确保图形界面（我这里是Swing项目）在构建本地镜像时能够正常工作。

2. -jar emailSender-1.0-SNAPSHOT.jar：指定要构建本地镜像的可执行 JAR 文件。

3. --no-fallback：禁用回退模式，强制使用本地镜像执行。

4. -H:ReflectionConfigurationFiles=reflect-config.json：指定反射配置文件的路径，以确保需要的类和方法在本地镜像中正确使用。

5. -H:Path=out：指定输出路径为 out文件夹，这样生成的本地镜像文件将保存在当前目录下的 out文件夹中。

6. --enable-url-protocols=https：启用 HTTPS 协议支持，以允许应用程序访问 HTTPS URL  

<br/>

<br/>

#### reflect-config.json

```json
[
  {
    "name": "com.resend.services.emails.model.CreateEmailOptions",
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true,
    "allDeclaredFields": true
  },
    {
    "name": "com.resend.services.emails.model.Attachment",
    "allDeclaredConstructors" : true,
    "allDeclaredMethods" : true,
    "allDeclaredFields" : true
  }
]
```

<br/>

#### 运行镜像文件

out目录中执行.exe文件即可
# GraalVM-Native-ImageSwing-Demo
