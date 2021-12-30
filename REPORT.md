#软工实训任务报告

#### [目录]()
#### [1.阅读和描述样例工程](#1)
##### [&emsp;1.1 理解与描述样例工程功能]()
##### [&emsp;1.2 UML类图描述代码结构组成]()
#### [2.标注样例工程的代码](#2)
#### [3.扩充和维护样例工程](#3)
#### [4.功能扩充点](#4)
#### [5.编写测试用例](#5)
<hr>


### 1.阅读和描述样例工程<span id=1/>
#### 1.1 理解与描述样例工程功能
&emsp;从 github 上 fork 整个样例工程到本地仓库后，分析和运行整个样例工程，精读样例工程代码。
- 首先分析样例工程中存在的类，找出每个类的用途。整个样例工程中共有6个类： Commmand 、CommandWords 、 Game 、 Main 、 Parser 和 Room
    - **Main**：该类只作为一个游戏启动的程序入口。main 方法中新建一个游戏主类对象，进行游戏启动操作。
    - **Command**：一个 Commmad 对象代表了用户输入的命令，它有一些方法可以很容易地判断是否是游戏的命令，还可以将命令中的第一个和第二个单词分离开来。
    - **CommandWords**：该类通过存储一个命令词汇字符串数组来定义游戏中所有有效的命令。
    - **Parser**：语法分析器从终端读入一行输入，将其解析为命令，并据此创建 Command 类对象。
    - **Roome**：一个Room对象代表游戏中的一个位置。房间可以由出口通到其它房间。
    - **Game**：该类是游戏的主体。它启动游戏，然后进入一个不断读取和执行输入的命令的循环。它也包括执行每一个用户命令的代码。
    <br>
- 项目的简单描述：
        该项目是一个简单的文字小游戏，游戏的基础结构是一个玩家在不同的房间移动，基于此，我们发挥想象力对该游戏进行扩展。
#### 1.2 UML类图描述代码结构组成
&emsp;通过 markdown 集成的 Mermaid 绘制如下类图：
``` mermaid
classDiagram
    class Game
    Game : -Parser parser
    Game : -Room currentRoom
    Game : -createRooms() void
    Game : +play() void
    Game : -printWelcome() void 
    Game : -processCommand(Command command) boolean
    Game : -printHelp() void
    Game : -goRoom(Command command) void
    Game : -quit(Command command) boolean

    class Parser
    Parser : -CommandWords commands
    Parser : -Scanner scanner
    Parser : +getCommand() Command
    Parser : +showCommands() void

    class Command
    Command : -String commandWord
    Command : -String secondWord
    Command : +getCommandWord() String
    Command : +getSecondWord() String
    Command : +isUnknown() boolean
    Command : +hasSecondWord() boolean

    class CommandWords
    CommandWords : -String[] validCommands
    CommandWords : +isCommand() boolean
    CommandWords : +showAll() void

    class Room
    Room : -String description
    Room : -HashMap<String,Room> exits
    Room : +setExit() void
    Room : +getExit() Room
    Room : +getShortDescription() String
    Room : +getLongDescription() String
    Room : -getExitString() String
    
    Game..>Command
    Game-->Parser
    Game-->Room
    Parser..>Command
    Parser-->CommandWords

```
### 2.标注样例工程的代码<span id=2/>
### 3.扩充和维护样例工程<span id=3/>
### 4.功能扩充点<span id=4/>
### 5.编写测试用例<span id=5/>


