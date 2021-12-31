#软工实训任务报告

#### [目录]()
#### [1.阅读和描述样例工程](#1)
##### [&emsp;1.1 理解与描述样例工程功能](#2)
##### [&emsp;1.2 UML类图描述代码结构组成](#3)
#### [2.标注样例工程的代码](#4)
#### [3.扩充和维护样例工程](#5)
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
&emsp;需要注意的几点问题：
- 类上的注释：描述类的作用，版本和作者信息

- 方法上的注释：对方法进行描述，其中描述内容可用html标签修饰；如果方法有参数和有返回值，必须对其进行说明

- 字段上的注释：多对复合字段的作用进行解释，以及用@see描述其相关方法
### 3.扩充和维护样例工程<span id=3/>
####&emsp;3.1 解决隐形耦合
&emsp;zuul游戏的用户界面是与英语的命令紧密绑定在一起的。假如希望改变界面使玩家可以使用其它语言，就需要找到源代码中所有命令字出现的地方，并加以修改，这是**隐形耦合**。
&emsp;(1) 额外添加一个枚举类型CommandWord,来解决以上问题：
```java
/**
 * 包含了游戏中所有的命令关键字
 * @author txg
 * @version 2021.12.21
 */
public enum CommandWord {
    Go,Quit,HELP,UNKNOWN;
}
```
&emsp;(2) 将 CommandWords 中存储命令字的数组改为 HashMap<String,CommandWord> 集合,通过字符串和 CommandWord 对象之间的映射来定义有效的命令而不是使用字符串数据来定义。
```java
/**
     * 构造方法，生成指令组对象
     */
    public CommandWords()
    {
        validCommands=new HashMap<>();
        //从枚举类型中获取命令
        validCommands.put("go",CommandWord.GO);
        validCommands.put("help",CommandWord.HELP);
        validCommands.put("quit",CommandWord.QUIT);
    }
```
&emsp;(3) 根据上一步，可以初步修改游戏的processCommand,使if选择语句中比较的commandWord类似不是String而是CommandWord枚举类型，可改写为如下：
```java
 CommandWord commandWord = command.getCommandWord();
        if (commandWord==CommandWord.HELP) {
            printHelp();
        }
        else if (commandWord==CommandWord.GO) {
            goRoom(command);
        }
        else if (commandWord==CommandWord.QUIT) {
            wantToQuit = quit(command);
        }
```
&emsp;(4) 使命令界面进一步解耦合，每当引入一个新的命令时，把用户输入文本与值的关联从CommandWords转移到CommandWord。具体修改的枚举类型如下：
```java
 //将命令字符串与枚举类型关联起来
    GO("go"),QUIT("quit"),HELP("help"),UNKNOWN("?");

    private String commandString;

    /**
     * 拥有命令字符串属性的的枚举类型
     * @param commandString 命令字符串
     */
    CommandWord(String commandString){
        this.commandString=commandString;
    }

    /**
     * 重写 toString() 方法，返回枚举类型代表的命令字符串
     * @return 返回对应的命令字符串
     */
    public String toString(){
        return commandString;
    }
```
&emsp;(5) 在第(4)步关联的基础上，修改 CommandWords 的初始化构成，使其直接从枚举中初始化所有指令。
```java
 public CommandWords()
    {
        validCommands=new HashMap<>();
        //从枚举类型中获取命令
       for(CommandWord commandWord:CommandWord.values()){
           if(commandWord!=CommandWord.UNKNOWN){
               validCommands.put(commandWord.toString(),commandWord);
           }
       }
    }
```
####&emsp;3.2 利用表驱动优化 if-else 语句
&emsp;(1) 由于每添加一个新的命令时，就得在一堆 if 语句中再加入一个分支，会导致代码膨胀臃肿，所以在上面用枚举类型解决完隐形耦合后，采取表驱动的方法进行优化。
```java
/**
 * 该类执行表驱动的作用，从而优化if-else结构<br>
 *
 * @author txg
 * @version 2021.12.31
 */
public class CommandTableDriven {
    /**
     * 每个指令对应的函数存储到 map 中,形成表驱动结构
     */
    private HashMap<CommandWord, Function<Command,Boolean>> table;
    private Game game;

    /**
     * 当表驱动对象初始化后，将<code>Game</code>中的处理业务进行一一对应，其函数的注册是用的lambda表达式<br>
     * 值得提醒的是：lambda 表达式中 Function 的传入参数为 Command 对象 ,返回参数为 Boolean。这是因为，通过发现，
     * 无论是已有功能还是扩展功能，我们对其的执行的服务的输入最多只有一个 Command 类型，输出最多为 Boolean 类型判断是否
     * 退出游戏。
     * @param game 游戏主体
     */
    public CommandTableDriven(Game game){
        this.game=game;
        table=new HashMap<>();
        //对每个命令，注册对应的函数
        table.put(CommandWord.GO,(command)->{...});
        table.put(CommandWord.HELP,(command)->{...});
        table.put(CommandWord.QUIT,command -> {... });
    }

    /**
     * 向<code>Game</code>对象中返回 map 的表驱动集合
     * @return 返回表驱动集合
     */
    public HashMap<CommandWord, Function<Command,Boolean>> getTable() {
        return table;
    }
}
```
&emsp;(2) 创建好表驱动类时，我们在 Game 类中需要实例化它，或者是增加其为 Game 类的属性，在游戏一开始时，就进行初始化。
```java
  /**
     * 表驱动类的实例对象，方便服务进行
     */
    private CommandTableDriven commandTableDriven;

    /**
     * 创建游戏并初始化内部数据和解析器
     */
    public Game()
    {
        createRooms();
        parser = new Parser();
        commandTableDriven=new CommandTableDriven(this);
    }
```
&emsp;(3) 最后将其运用到 parseCommand 方法中，处理命令的服务，以下展示改善前后的对比：

**改善前**
```java
 private boolean processCommand(Command command)
    {  boolean wantToQuit = false;

       if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }
        //获取命令枚举类型
        CommandWord commandWord = command.getCommandWord();
        if (commandWord==CommandWord.HELP) {
            printHelp();
        }
        else if (commandWord==CommandWord.GO) {
            goRoom(command);
        }
        else if (commandWord==CommandWord.QUIT) {
            wantToQuit = quit(command);
        }
        
        // else command not recognised.
        return wantToQuit;
         
    }
```
**改善后**
```java
private boolean processCommand(Command command)
    {  boolean wantToQuit = false;

       if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }
        //获取命令枚举类型
        CommandWord commandWord = command.getCommandWord();
        //获取驱动表，执行该命令对应的函数，返回值赋予 wantToQuit
        wantToQuit=commandTableDriven.getTable().get(commandWord).apply(command);
        // else command not recognised.
        return wantToQuit;
    }
```
&emsp;可以发现，通过表驱动的方法修改 if-else 语句，可以使 Game 游戏本体中的代码不会在扩展功能后出现膨胀臃肿的情况，且我们的业务方法与主体进行了分离，即使以后增加了新的方法，Game 中的 processCommand 方法不需要任何修改。

### 4.功能扩充点<span id=4/>
#### &emsp;4.1 房间中增加物体+look指令
&emsp;&emsp;(1)在 entity 包中新加一个类 Item ，表示物体类。Item 与 Room 是关联关系，且是 Room 单向关联 Item。 Item 具体属性与构造方法如下：
```java
 private String description;
    private float weight;

    /**
     * 构造方法，对物体对象初始化
     * @param description 物体的描述
     * @param weight 物体的重量
     */
    public Item(String description, float weight) {
        this.description = description;
        this.weight = weight;
    }
```
&emsp;&emsp;(2)上述单独创建 Item 类，是遵循了类的内聚原则，即类必须表示的是一个单独的、定义明确的实体。当在 Room 房间中存在物体时，物体的存储目前选择的是 HashSet 集合，方便对房间中的物体进行操作。
```java
private String description;
    /**
     * 用 HashMap 存放了一个房间的各种出口与其对应房间的 key-value 值
     */
    private HashMap<String, Room> exits;
    private HashSet<Item> items;

    /**
     * 构造方法，初始化房间的描述和<code>HashMap</code>容器
     * @param description 对房间的描述
     */
    public Room(String description)
    {
        this.description = description;
        exits = new HashMap<>();
        items = new HashSet<>();
    }
```
&emsp;&emsp;(3) look 指令的实现。因为前面对 if-else 进行了优化，且生成了命令字段的枚举类型，所以我们可以很方便的添加 look 指令和在表驱动中添加对应的服务。不过我们先展示 look 的具体功能，即描述当前房间信息+物品信息：
```java
 public String getLongDescription()
    {
        return "You are " + description + ".\n" + getItemsDescription()+getExitString();
    }

    /**
     * 获取所有物品的描述信息
     * @return 物品们的描述信息
     */
    public String getItemsDescription(){
        if(items.isEmpty())
            return "这个房间啥都没有"+'\n';
        StringBuilder s = new StringBuilder("");
        for(Item item:items){
            s.append(item.getDescription()+"\t"+item.getWeight()+"kg"+"\n");
        }
        return "仔细观察这个房间:\n"+s.toString();
    }
```
&emsp;&emsp;(4)然后是在表枚举类型中加入 LOOK 类型，并且将其对应的服务加入 表驱动类中就完成了任务。其余类中不需要修改什么代码。
```java
//将命令字符串与枚举类型关联起来
    GO("go"),QUIT("quit"),
    HELP("help"),Look("look"),
    UNKNOWN("?");
```
```java
    table.put(CommandWord.Look,command -> {
        System.out.println(game.getCurrentRoom().getLongDescription());return false;});
}
```
#### &emsp;4.2 实现 back 指令，将玩家带回上一个场景


### 5.编写测试用例<span id=5/>


