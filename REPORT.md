# 软工实训任务报告

#### [目录]()
#### [1.阅读和描述样例工程](#1)
#### [2.标注样例工程的代码](#2)
#### [3.扩充和维护样例工程](#3)
#### [4.功能扩充点](#4)
#### [5.编写测试用例](#5)
<hr>


## 1.阅读和描述样例工程<span id=1/>
### 1.1 理解与描述样例工程功能
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
### 1.2 UML类图描述代码结构组成
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
    Game*--Parser
    Game*--Room
    Parser..>Command
    Parser*--CommandWords

```
&ensp;经由分析：
- 因为 Game 中有 Command 参与的方法，所有与 Command 为依赖关系
- 在 Game 构造中就创建了 Room ，所以很强的关联， Game 和 Room 为组合关系
- 同样 Parser 是与 Game 是强关联的，Parser 随着 Game 消失而消失，为组合关系
- Parser 构造中对 CommandWords 初始化，为组合关系
- Parser 有依赖 Commadn 的方法，所以为依赖关系
## 2.标注样例工程的代码(注释的风格检验放入的第三部分)<span id=2/>
&emsp;需要注意的几点问题：
- 类上的注释：描述类的作用，版本和作者信息

- 方法上的注释：对方法进行描述，其中描述内容可用html标签修饰；如果方法有参数和有返回值，必须对其进行说明

- 字段上的注释：多对复合字段的作用进行解释，以及用@see描述其相关方法
## 3.扩充和维护样例工程<span id=3/>
### 3.1 解决隐形耦合(消除魔法值)
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
&emsp;(6) 之后对枚举类型再进行一个小扩展，使得用户在输入 help 后，可以看到更加详细的命令解释。我们将枚举类型的属性在扩展一个 description ，且生成一个 getter 方法，当展示所有命令时，除了输出原本的命令字符串，还输出这个 description 信息。enum 修改如下:
```java
public enum CommandWord {
    //将命令字符串与枚举类型关联起来
    GO("go","前往一个地方"),QUIT("quit","退出游戏"),
    HELP("help","获取帮助"),LOOK("look","仔细观察所处地方"),
    BACK("back","回到上一个地方"),UNKNOWN("?",""),
    TAKE("take","拿起东西"),DROP("drop","丢掉东西"),
    ITEMS("items","展示所有物品"),EAT("eat","吃"),
    INFO("info","展示玩家信息");

    private String commandString;
    private String description;

```
&emsp;展示所有命令指令方法修改如下:
```java
 /**
     * 控制台打印所有的行为指令
     */
    public void showAll()
    {
        for(String s: validCommands.keySet()){
            System.out.println(s+" "+validCommands.get(s).getDescription());
        }
        System.out.println();
    }
```
### 3.2 利用表驱动优化 if-else 语句
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
### 3.3 代码质量分析和规范检查
&emsp;**(1)质量分析工具：Alibaba Java Coding Guidelines**
&emsp;&emsp;这个插件可在 IDEA 插件商店中下载，下载重启 IDEA 后，右击项目名后选择“编码规约扫描”即可对整个项目进行扫描。扫描结束后，通常有3中结果，blocker、critical、major。其中 blocker 会在项目中报红色，必须进行修改。而 critical 和 major 是程度比较低的代码质量隐患。

**&emsp;修改过的 blocker 问题:**
- 在 if 选择语句中，即使只有一句表达式，也必须使用大括号,如下所示：
```java
 public boolean hasExit(String description){
        if(exits.containsKey(description)) {
            return true;
        }
        return false;
    }
```
- 重写的方法，必须要添加 @Overried
```java
    @Override
    public String toString(){
        return commandString;
    }
```
**&emsp;存在的 cirtical 问题:**
- 新建引用类型必须重写 hasCode 和 equals 方法

**&emsp;存在的 major 问题:**
- 不允许任何魔法值直接出现在代码中
- 及时清理不再使用的代码段和配置信息
- 方法内部的单行注释，应放在另起一行放在被注释语句上方。方法内部多行注释使用/* */注释。注意与代码对齐

&emsp;**(2)风格检查工具：CheckStyle**
&emsp;&emsp;这个插件可在 IDEA 插件商店中下载，用于对代码风格进行检测。对本次样例工程，使用的是 Google 规范的风格检查。
&emsp;&emsp;样例工程中主要修改的是注释的规范，其中最多出现的问题是：
- 注释内容完成后要有结尾符号
- @后面的内容前面要有空行

## 4.功能扩充点(具体实验截图位于实践报告中)<span id=4/>
### &emsp;4.1 房间中增加物体+look指令
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
&emsp;&emsp;(5)当用户在控制台输入 look 后，即显示用户所在房间的详细信息以及该房间的所有物品信息和出口信息。
### &emsp;4.2 实现 back 指令，将玩家带回上一个场景
&emsp;&emsp;(1) 按照需求，back 指令将玩家带回上一个场景。所以我的想法是在用户执行 go 命令成功进入下一个房间之前，将本房间作为下一个房间的一个出口，出口名叫 “back”。此处代码更改只涉及表驱动类 CommandTableDriven ：
```java
if (nextRoom == null) {
                System.out.println("There is no door!");
            }
            else {
                //在进去下一个房间前，将这个房间的信息给back,则可以通过back返回上一个房间
                nextRoom.setExit("back",game.getCurrentRoom());
                game.setCurrentRoom(nextRoom);
                System.out.println(game.getCurrentRoom().getLongDescription());
            }
```
&emsp;&emsp;(2)在 Room 方法中额外添加一个方法，用于返回某个出口是否存在，以便于之后判断是否有房间无上一个房间的记录(即起点)。
```java
/**
     * 判断是否存在某个出口
     * @param description 出口名
     * @return 存在则返回 true,否则返回 false
     */
    public boolean hasExit(String description){
        if(exits.containsKey(description))
            return true;
        return false;
    }
```
&emsp;&emsp;(3)在表驱动 Map 中添加 back 对应的功能实现的函数，具体实现如下：
```java
 // back 指令对应的功能，回退到上一个房间
        table.put(CommandWord.BACK,command -> {
            if(game.getCurrentRoom().hasExit("back")){
                command.setSecondWord("back");
                table.get(CommandWord.GO).apply(command);
            }else {
                System.out.println("这已经是起点...");
            }
            return false;
        });
```
&emsp;&emsp;(4)该功能的检验如下：
- 当用户一开始就输入 back 时，控制台输出 “这已经是起点...” ，表示起点无上一个房间
- 当进入了其它房间后，输入单独的一个 back ，正确的回到上一个房间
- 当进入了其它房间后，输入 back 后仍跟其它符号，仍识别 back 回到上一个房间
- 当多次 go ,又回到 起点 时，这时起点也会有一个 back ，使用 back 指令可以回到上一个房间

### &emsp;4.3 更高级的 back ,可多次回退，直至回到起点
&emsp;&emsp;(1)我们上一步实现的 back 可以实现针对当前房间的回退，是因为将上一个房间作为一个新的名为 "back" 的出口进行了存储。，这样的问题是玩家一旦从起点出发后，虽然可以多次回退，但无法回退正确的步数到起点。所以，这次采取**栈**来存储历史房间记录，首先展示一些，栈的定义与初始化：
```java
 /**
     * back的回退栈，记录着玩家去过的场景
     */
    private Stack<Room> backs;
    /**
     * 创建游戏并初始化内部数据和解析器
     */
    public Game() {
        //初始化栈
        backs=new Stack<>();
        //创建所有房间
        createRooms();
        //初始化解析器
        parser = new Parser();
        //表驱动
        commandTableDriven=new CommandTableDriven(this);
    }

```
&emsp;&emsp;(2)同样，在 go 命令去另一个房间时，将当前房间压入栈，表示生成一个历史房间记录：
```java
 //进入了另一个房间，将上一个房间入栈
    game.getBacks().push(game.getCurrentRoom());
    game.setCurrentRoom(nextRoom);

    System.out.println(game.getCurrentRoom().getLongDescription());
```
&emsp;&emsp;(3)在表驱动中，修改 back 的功能代码，判断**栈**的状态，如果栈为空，则表示无法回退，玩家已在起点；如果栈不为空，取出栈顶房间，作为 game 的当前房间，表示对进入上一个房间，并打印描述信息：
```java
 table.put(CommandWord.BACK,command -> {
            Stack<Room> stack = game.getBacks();
            //对栈顶元素进行判断
            if(stack.size()!=0){
                game.setCurrentRoom(stack.pop());
                System.out.println(game.getCurrentRoom().getLongDescription());
            }else{
                System.out.println("这已经是起点了...");
            }
            return false;
        });
```
&emsp;&emsp;(4)该功能的检验如下
- 玩家有多少次 go 命令，就可以实现多少次 back 命令
- back 可以多次回退，直至回到起点，不再回退
- back 命令单独其作为，后面可空格后跟其它参数但不影响
### &emsp;4.4 在游戏中增加具有传输功能的房间
&emsp;&emsp;(1)如果要实现随机传送的话，需要知道所有房间对象信息才可以进行随机选择。这里我们在 Game 中再创建一个 ArrayList 数组 (目前Game中已存在比较多的属性字段了，这并不是好事，所以在之后会尽早重构)
```java
 /**
     * 创建游戏并初始化内部数据和解析器
     */
    private ArrayList<Room> rooms;
    public Game() {
        //初始化栈
        backs=new Stack<>();
        //初始化对所有房间的存储数组列表
        rooms=new ArrayList<>();
        //创建所有房间
        createRooms();
        //初始化解析器
        parser = new Parser();
        //表驱动
        commandTableDriven=new CommandTableDriven(this);
    }
```
&emsp;&emsp;(2)因为有传送点的房间也是一个 Room ,所以在 Room 中添加了一个属性来检验是否为传送房间。并在即将进入这个传送房间前，进行逻辑判断，判断逻辑在 go 中：
```java
 //检验下一个房间是否有秘密传送点
if(nextRoom.isTransferPoint()){
    //输出nextRoom的描述
    System.out.println(nextRoom.getShortDescription());
    ArrayList<Room> rooms = game.getRooms();
    //通过随机数实现随机传输到另一个房间
    int i =(int)(Math.random()*rooms.size());
    nextRoom = rooms.get(i);
    //保证房间不相同
    while(nextRoom == game.getCurrentRoom()){
        i =(int)(Math.random()*rooms.size());
        nextRoom = rooms.get(i);
    }
```
&emsp;&emsp;(3)该功能检验如下：
- 当从一个房间进入传送房间时：输出传送房间的描述话语，并说明将玩家进行传送，将玩家传送到一个随机的另一个房间
- 可以在传送后的房间中使用 back 回到传送前的房间
- 因为房间只是充当一个秘密传送门，所以并没有存放物品

### &emsp;4.5 实现 Player 玩家类,玩家类的5个小功能
&emsp;&emsp;**(1)创建玩家类，属性字段通过考虑之后的扩展，有：姓名，最大承受重量，身上现有重量，身上所有物品，当前所处房间，历史房间:**
```java
public class Player {
    private String name;
    private float maxBearWeight;
    private float nowWeight;
    private Set<Item> items;
    private Room currentRoom;
    private Stack<Room> room_history;

    /**
     * 构造方法，实例化玩家类.
     *
     * @param name 玩家信息
     * @param maxBearWeight 玩家最大承受重量
     */
    public Player(String name, float maxBearWeight,float nowWeight) {
        this.name = name;
        this.maxBearWeight = maxBearWeight;
        this.nowWeight=nowWeight;
        //初始化集合
        items=new HashSet<>();
        room_history=new Stack<>();
    }
```
&emsp;&emsp;**(2)在第(1)步，我们可以将 Game 类中的属性 currentName 转移到玩家类中，因为有了玩家类，当前房间更像是玩家的信息，并且历史房间的存储应该也是针对玩家的。所以以上算是对系统的一次重构。接下来，在 Player 中实现方法使玩家对象可以添加物品，判断可携带物品是否超过重量上限。**
```java
 /**
     * 添加物品
     * @param item 物品对象
     * @return 可以添加为 true,不能添加为 flase
     */
    public boolean addItem(Item item){
        if(!isOver(item)){
            items.add(item);
            nowWeight+=item.getWeight();
            return true;
        }
        return false;
    }
       /**
     * 丢弃player身上的物品
     * @param item 丢弃物品
     */
   public void dropItem(Item item){
        items.remove(item);
        nowWeight-=item.getWeight();
   }
    /**
     * 判断加入某个物体后是否超重
     * @param item 物体对象
     * @return 超重为true，否则为flase
     */
    public boolean isOver(Item item){
        return maxBearWeight<item.getWeight()+nowWeight;
    }
```
&emsp;&emsp;**(3)实现 take 和 drop 命令。首先我们将 Player 玩家对象在 Game 中创建，对表驱动中的功能方法进行重构之后的修改。修改如下：**
```java
 /**
     * 目前只有一个玩家，所有并没有用集合存储
     */
    private Player player;

    /**
     * 表驱动类的实例对象，方便服务进行
     */
    private CommandTableDriven commandTableDriven;

    private ArrayList<Room> rooms;

    /**
     * 创建游戏并初始化内部数据和解析器
     */
    public Game() {
        //初始化对所有房间的存储数组列表
        rooms=new ArrayList<>();
        //创建玩家对象(目前只有一个)
        createPlayers();
        //创建所有房间
        createRooms();
        //初始化解析器
        parser = new Parser();
        //表驱动
        commandTableDriven=new CommandTableDriven(this,player);
    }
```
&emsp;&emsp;以上是对 Game 中字段的重构。接下来是对表驱动的重构，因为整个游戏是一个整体，而玩家是不同的个体，所有我们将表驱动类的传参改成了(game,player)以针对不同的玩家进行操作。
```java
public class CommandTableDriven {
    /**
     * 每个指令对应的函数存储到 map 中,形成表驱动结构
     */
    private final HashMap<CommandWord, Function<Command,Boolean>> table;
    private final Game game;
    private final Player player;

    /**
     * 当表驱动对象初始化后，将<code>Game</code>中的处理业务进行一一对应，其函数的注册是用的lambda表达式<br>
     * 值得提醒的是：lambda 表达式中 Function 的传入参数为 Command 对象 ,返回参数为 Boolean。这是因为，通过发现，
     * 无论是已有功能还是扩展功能，我们对其的执行的服务的输入最多只有一个 Command 类型，输出最多为 Boolean 类型判断是否
     * 退出游戏。
     * @param game 游戏主体
     */
    public CommandTableDriven(Game game,Player player){
        //初始化 game
        this.game=game;
        //初始化 player
        this.player=player;
        //初始化驱动表
        table=new HashMap<>();
```
&emsp;&emsp;然后是实现 take 和 drop 指令。当玩家执行 take 时，首先判断物品是否存在，如果存在则继续判断是否容量支持，再决定是否添加物品。当玩家成功添加后，该物品不属于当前房间，而属于玩家；当玩家执行 drop 时，首先判断玩家是否有该物品，如果有，玩家进行 drop ,物品不属于玩家，而属于当前房间。它们的实现在表驱动类中，具体代码如下：
```java
 //玩家的拿东西指令
        table.put(CommandWord.TAKE,command -> {
            if(!command.hasSecondWord()) {
                // if there is no second word, we don't know take what...
                System.out.println("Take what?");
                return false;
            }
            //获取物品名称
            String name = command.getSecondWord();
            //查询是否有这个物品在房间中
            Item item = player.getCurrentRoom().getItem(name);
            if(item==null){
                System.out.println("这个房间没那个东西");
            }else {
                //player拿东西
                //先判断是否拿的动
                if(player.addItem(item)){
                    player.getCurrentRoom().getItems().remove(item);
                    System.out.println("你成功拿起了"+item.getName()+",你背包剩余容量为:"+(player.getMaxBearWeight()-player.getNowWeight())+"kg");
                }else {
                    System.out.println("你拿不动:"+item.getName()+" "+item.getWeight()+"kg"+" ，"+"而你容量只有:"+(player.getMaxBearWeight()-player.getNowWeight())+"kg");
                }
            }
            return false;
        });

        //玩家的丢东西指令
        table.put(CommandWord.DROP,command -> {
            if(!command.hasSecondWord()) {
                // if there is no second word, we don't know take what...
                System.out.println("Drop what?");
                return false;
            }
            //获取物品名称
            String name = command.getSecondWord();
            //查询这个物品是否在玩家身上
            Item item = player.getItem(name);
            if(item==null){
                System.out.println("你身上没那个东西！！！");
            }else {
                player.dropItem(item);
                System.out.println("你成功丢掉了"+item.getName()+",你背包剩余容量为:"+(player.getMaxBearWeight()-player.getNowWeight())+"kg");
                System.out.println(player.showItems());
                 //丢掉的物品放入当前房间中
                player.getCurrentRoom().addItem(item);
            }
            return false;
        });
```
&emsp;&emsp;**(4)游戏中增加一个新命令“Items”，打印所有物品信息。如果房间无物品，则打印房间什么都没有，如果人物没物品，则打印你身上什么也没有。**
```java
 //展示所有物品信息
        table.put(CommandWord.ITEMS,command -> {
            System.out.println(player.getCurrentRoom().getItemsDescription());
            System.out.println(player.showItems());
            return false;
        });
```
&emsp;&emsp;**(5)随机在房间中生成魔法饼干，用户执行“eat cookie”命令，吃掉后可增加负重。**
```java
/**
     * 随机给一个非传送房间加入魔法饼干
     */
    private void addCookie(){
        //随机获得一个非传送房间的房间
        int i=(int)(Math.random()*rooms.size());
        //给这个房间添加魔法饼干
        rooms.get(i).addItem(new Item("魔法饼干","有魔力一般，能让你更耐受",0.1f));
    }
```
&emsp;&emsp;在添加完魔法饼干后，在表驱动类中实现 eat magic 的功能。具体代码如下：
```java
//玩家执行吃magic cookie
table.put(CommandWord.EAT,command -> {
    if(!command.hasSecondWord()) {
        // if there is no second word, we don't know eat what...
        System.out.println("EAT what?");
        return false;
    }
    //获取物品名称
    String name = command.getSecondWord();
    //查询是否有这个物品在房间中
    Item item = player.getCurrentRoom().getItem(name);
    if(item==null){
        System.out.println("这个房间没有这个东西");
    }else {
        if(item.getName().equals("魔法饼干")){
            //玩家增加耐受容量+20kg
            player.setMaxBearWeight(player.getMaxBearWeight()+20);
            System.out.println("你吃了这个魔法饼干，感觉力大无穷，神清气爽，耐受+20kg"+"你现在的容量为："+player.getMaxBearWeight()+"kg");
            //把魔法饼干从房间移除
            player.getCurrentRoom().getItems().remove(item);
        }else {
            System.out.println("这个东西不能吃！！！");
        }
    }
    return false;
});
```
### &emsp;4.6 控制台展示玩家所有信息 'info'
&emsp;&emsp;当游戏玩家想时时了解自己的状态时，我们提供 "info" 命令使玩家可以了解自身信息
```java
 /**
     * 完整的描述自己
     * @return 返回描述字段
     */
   public String getSelfLongDescription(){
       return "姓名："+name+"\n"
               + "最大耐受量:"+maxBearWeight+"kg"+"\n"
               +"目前耐受量:"+nowWeight+"kg"+"\n"
               +"剩余耐受量:"+(maxBearWeight-nowWeight)+"kg"+"\n"
               +"目前所在："+getCurrentRoom().getShortDescription()+"\n"
               +showItems();
   }
```
&emsp;&emsp;表驱动类中，增加 info 的功能实现代码
```java
 //展示玩家具体信息
        table.put(CommandWord.INFO,command -> {
            System.out.println("你仔细的审查者自己：");
            System.out.println(player.getSelfLongDescription());
           return false;
        });
```
### &emsp;4.7 额外增加向上和向下的房间移动
&emsp;&emsp;玩家除了东南西北移动外，应该还可以上下移动，因为每个房间对应的出口及其房间信息是由 HashMap 表存储的，所以只要在初始化对房间添加 up 或者 down 的移动方向以及移动去往的房间就行。代码如下：
```java
 theater.setExit("up",forest);
```

## 5.编写测试用例(实验截图见实践报告)<span id=5/>
整个单元测试共分为两部分：一个是新增枚举类型与其相关类的测试，因为枚举类型的增加，间接的改变了其它类中方法的代码；二是对增加的表驱动类中的7个大的扩展功能进行单元测试(其中第5个Player玩家扩展功能又包括5个小功能)。
### 5.1 枚举类型 CommandWord 测试
&emsp;新增的枚举类型 CommandWord 一共有两个功能：(1) 将命令字符串以重写的 toString 方法返回；(2) getter 方法，获得该命令字符串的具体描述，帮助玩家理解行为指令。具体测试如下：
```java
import junit.framework.TestCase;
import org.junit.Test;

public class CommandWordTest extends TestCase {
    CommandWord commandWord = CommandWord.GO;
    @Test
    public void testTestToString() {
        //应该为“go”，不为“go”则判断为false
        assertEquals( "go", commandWord.toString());

    }
    @Test
    public void testGetDescription() {
        //应该为“前往一个地方”，不为“前往一个地方”则判断为false
        assertEquals("前往一个地方", commandWord.getDescription());
    }

}

```
&emsp;上述方法的单元测试均通过。

### 5.2 CommandWords 类测试
&emsp;CommandWords 中存放的正是玩家执行的指令组，因为以前存放的都是魔法值，存在隐形耦合的问题，所以当我们加入枚举类型后，我们在 CommandWords 属性增加一个 HashMap 集合存放【命令字符串与枚举类型的键值对】。方便我们进行转换和判断。
&emsp;CommandWords 中共有3个功能：(1)判断某个输入指令是否为游戏中可用的行为指令；(2)获得某个字符串对应的 CommandWord 枚举对象；(3) 控制台打印所有的行为指令。
```java
import cn.edu.whut.sept.zuul.enums.CommandWord;
import junit.framework.TestCase;
import org.junit.Test;

public class CommandWordsTest extends TestCase {
    CommandWords commandWords = new CommandWords();
    @Test
    public void testIsCommand() {
        //输入help,判断help是否为可用的行为指令,help是可用指令,结果为true
        assertEquals(true,commandWords.isCommand("help"));
        //输入goo,为无用的行为指令，结果为false
        assertEquals(false,commandWords.isCommand("goo"));
    }
    @Test
    public void testGetCommandWord() {
        //该方法的返回值应该是 CommandWord.BACK
        assertEquals(CommandWord.BACK, commandWords.getCommandWord("back"));
    }
    @Test
    public void testShowAll() {
        //控制台输出正确，方法正确
        commandWords.showAll();
    }
}
```
&emsp;上述方法的单元测试均通过。
### 5.3 Game 类的测试
&emsp;Game 类中是游戏的主体类型，其中因为枚举类型的增加和用表驱动方法进行优化 if-else 语句的影响，方法 processCommand 代码修改，需要进行测试；另外在扩展功能中，将随机生成魔法饼干的功能也属于 Game 类，所以进行测试。
&emsp;但需要注意的是，因为 Game 中的 processCommand 和 addCookie 是私有方法，所以需要通过**反射**机制来获取 Method 间接执行私有方法。具体测试如下：
```java
public class GameTest extends TestCase {
    //生成game对象
    Game game = new Game();
    //获得Game的Class类型
    Class c = Game.class;
    //待定Method类型
    Method method;

    /**
     * 测试 processCommand 方法.
     */
    @Test
    public void testProcessCommand() {
        //反射获取
        try {
            method = c.getDeclaredMethod("processCommand",new Class[]{Command.class});
            //使私有可执行
            method.setAccessible(true);
            //传入 new Command(CommandWord.GO,"east") 对象
            method.invoke(game,new Object[]{new Command(CommandWord.GO,"east")});
        } catch (Exception e) {
            e.printStackTrace();
        }

        //该测试结果应在控制台输出：
        //你发现自己在 in a lecture theater.
        //这个地方啥都没有
        //Exits: west up

        //具体测试结果一致。即可用执行 Command 指令的执行
    }

    /**
     * 增加Cookie.
     */
    @Test
    public void testAddCookie() {
        //反射获取
        try {
            method = c.getDeclaredMethod("addCookie");
            //使私有可执行
            method.setAccessible(true);
            //空参方法，执行完以后，房间中会多增加一块饼干
            method.invoke(game);
            //查询所有房间中cookie的数量，总cookie数应该是2块，因为初始化也调用了addCookie方法
            int count=0;
            for (Room room : game.getRooms()) {
                if (room.getItem("魔法饼干")!=null) {
                    count++;
                }
            }
            //count数量应该为2
            assertEquals(2,count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
```
&emsp;上述方法的单元测试均通过。

### 5.4 Item 类(物品类)测试
&emsp;Item 类是新增扩展功能时创建的类，它关联于 Room 类 或 Player 类。其中的主要功能为：(1)获得物体的简短描述(2)获得物体的长描述(3)获得物体的重量(4)获得物体的名字。
&emsp;其方法目标明确，测试代码如下:
```java
public class ItemTest extends TestCase {
    Item item = new Item("西瓜", "一个大西瓜", 1);
    @Test
    public void testGetLongDescription() {
        String s = "西瓜" + "\t" + "一个大西瓜" + "\t" + "1.0";
        //测试应该正确
        assertEquals(s,item.getLongDescription());
    }
    @Test
    public void testGetShortDescription() {
        String s = "西瓜" + "\t" +"1.0";
        //测试应该正确
        assertEquals(s,item.getShortDescription());
    }
    @Test
    public void testGetWeight() {
        float f = 1.0f;
        //测试应该正确
        assertEquals(f,item.getWeight());

    }
    @Test
    public void testTestGetName() {
        String name = "西瓜";
        //测试应该正确
        assertEquals(name,item.getName());
    }
}
```

### 5.5 Player 类(玩家类)测试
&emsp;Player 是扩展功能 5 添加的新的类型，它是游戏玩家的实例，将 Game 的 currentRoom 字段重构入 Player 中，是类的内聚程度增加的表现，有利于之后功能的添加。Player 中有许多描述自身信息和利于游戏逻辑功能实现的代码：(1)增加物品到玩家身上 (2)测试是否加入一个物体会超重 (3)从玩家物品列表中获取一个物品 (4)丢弃Player身上的物品 (5)展示玩家的详细信息。
&emsp;具体测试代码如下：
```java
public class PlayerTest extends TestCase {
    @Test
    public void testAddItem() {
        Player player = new Player("TXG", 100, 0);
        //因为 player 初始化时自动初始化存放 Item 的集合，所有通过添加 item,看是否集合中元素+1
        int count1 = player.getItems().size();
        player.addItem(new Item("1","1",0));
        int count2 = player.getItems().size();
        //应该测试正确
        assertEquals(count2,count1+1);
    }
    @Test
    public void testIsOver() {
        Player player = new Player("TXG", 100, 0);
        Item item = new Item("石头","一块石头",50);
        Item item1 = new Item("大石头","一块大石头",120);
        //均测试成功
        assertEquals(false, player.isOver(item));
        assertEquals(true, player.isOver(item1));
    }
    @Test
    public void testGetItem() {
        Player player = new Player("TXG", 100, 0);
        Item item = new Item("石头","一块石头",50);
        player.addItem(item);
        //可用找到，测试成功
        assertEquals(item,player.getItem("石头"));
    }
    @Test
    public void testDropItem() {
        Player player = new Player("TXG", 100, 0);
        Item item = new Item("石头","一块石头",50);
        player.addItem(item);
        player.dropItem(item);
        //正确
        assertEquals(0, player.getItems().size());
    }
    @Test
    public void testShowItems() {
        Player player = new Player("TXG", 100, 0);
        Item item = new Item("大石头","一块大石头",50);
        Item item1 = new Item("石头","一块石头",10);
        player.addItem(item);
        player.addItem(item1);
        String s = "你身上有:\n"
                + "大石头" + "\t"+"50.0kg\n"
                + "石头" + "\t"+"10.0kg\n";
        //正确
        assertEquals(s,player.showItems());
    }
    @Test
    public void testGetSelfLongDescription() {
        Player player = new Player("TXG", 100, 0);
        Item item = new Item("大石头","一块大石头",50);
        Item item1 = new Item("石头","一块石头",10);
        player.addItem(item);
        player.addItem(item1);
        player.setCurrentRoom(new Room("一个房间"));
        String s = "姓名：TXG\n"
                + "最大耐受量:100.0kg\n"
                + "目前耐受量:60.0kg\n"
                + "剩余耐受量:40.0kg\n"
                + "目前所在：一个房间\n"
                + "你身上有:\n"
                + "大石头\t50.0kg\n"
                + "石头\t10.0kg\n";
        //正确
        assertEquals(s,player.getSelfLongDescription());
    }
}
```

### 5.6 CommandTableDriven 表驱动类测试
&emsp;CommandTableDriven 中 存储了一个 HashMap，键值对为<命令，对应的功能方法>，其中对应的功能方法都是 lambda 表达式写入的，即是一个 Function<Command, Boolean> 类 , 其中根据功能方法的相似性，所有的方法的传入参数均为 Command 命令对象，返回值为 Boolean 类型 来判断是否退出程序。
**(1)对 go 命令的测试**
```java
    @Test
    public void testGo(){
        Command command = new Command(CommandWord.GO,"east");
        //正确
        commandTableDriven.getTable().get(CommandWord.GO).apply(command);
        //理论输出:
        //你发现自己在 in a lecture theater.
        //这个地方啥都没有
        //Exits: west up

    }
```
**(2)对 back 命令的测试**
```java
    @Test
    public void testBack(){
        Command command = new Command(CommandWord.GO,"east");
        Command command1 = new Command(CommandWord.BACK,null);
        //正确
        commandTableDriven.getTable().get(CommandWord.GO).apply(command);
        commandTableDriven.getTable().get(CommandWord.BACK).apply(command1);
        //输出如下语句：
//        你发现自己在 in a lecture theater.
//                这个地方有:
//        魔法饼干	有魔力一般，能让你更耐受	0.1kg
//        Exits: west up
//        你发现自己在 outside the main entrance of the university.
//        这个地方有:
//        画	一副奇怪的画，但看不懂画的是什么	0.2kg
//        大石头	一块奇怪的大石头	60.0kg
//        Exits: east south west
    }
```

**(3)对 help 命令的测试**
```java
  @Test
    public void testHelp(){
        Command command = new Command(CommandWord.HELP,null);
        //正确
        commandTableDriven.getTable().get(CommandWord.HELP).apply(command);
        //输出如下语句：
        //Your command words are:
        //drop 丢掉东西
        //help 获取帮助
        //take 拿起东西
        //go 前往一个地方
        //back 回到上一个地方
        //eat 吃
        //quit 退出游戏
        //look 仔细观察所处地方
        //items 展示所有物品
        //info 展示玩家信息

    }
```
**(4)对 quit 命令的测试**
```java
 @Test
    public void testQuit(){
        Command command = new Command(CommandWord.QUIT,null);
        boolean flag = commandTableDriven.getTable().get(CommandWord.QUIT).apply(command);
        //返回true，正确退出
        assertEquals(true, flag);
    }
```

**(5)对 take 命令的测试**
```java
    @Test
    public void testTake(){
        Command command = new Command(CommandWord.TAKE,"画");
        //正确
        commandTableDriven.getTable().get(CommandWord.TAKE).apply(command);
        //输出如下语句：
        //你成功拿起了画,你背包剩余容量为:49.8kg
    }

```

**(6)对 drop 命令的测试**
```java
    @Test
    public void testDrop(){
        Command command = new Command(CommandWord.TAKE,"画");
        commandTableDriven.getTable().get(CommandWord.TAKE).apply(command);
        Command command1 = new Command(CommandWord.DROP,"画");
        //正确
        commandTableDriven.getTable().get(CommandWord.DROP).apply(command1);
        //输出如下语句：
        //你成功拿起了画,你背包剩余容量为:49.8kg
        //你成功丢掉了画,你背包剩余容量为:50.0kg
        //你身上啥都没有
    }
```

**(7)对 look 命令的测试**
```java
    @Test
    public void testLook(){
        Command command = new Command(CommandWord.LOOK,null);
        commandTableDriven.getTable().get(CommandWord.LOOK).apply(command);
        //输出如下语句：
        //你发现自己在 outside the main entrance of the university.
        //这个地方有:
        //画	一副奇怪的画，但看不懂画的是什么	0.2kg
        //大石头	一块奇怪的大石头	60.0kg
        //Exits: east south west
    }
```
**(8)对 items 命令的测试**
```java
    @Test
    public void testItems(){
        Command command = new Command(CommandWord.ITEMS,null);
        commandTableDriven.getTable().get(CommandWord.ITEMS).apply(command);
        //输出如下语句：
        //这个地方有:
        //画	一副奇怪的画，但看不懂画的是什么	0.2kg
        //大石头	一块奇怪的大石头	60.0kg
        //
        //你身上啥都没有
    }
```
**(9)对 info 命令的测试**
```java
    @Test
    public void testInfo(){
        Command command = new Command(CommandWord.INFO,null);
        commandTableDriven.getTable().get(CommandWord.INFO).apply(command);
        //输出如下语句：
        //你仔细的审查者自己：
        //姓名：TXG
        //最大耐受量:50.0kg
        //目前耐受量:0.0kg
        //剩余耐受量:50.0kg
        //目前所在：outside the main entrance of the university
        //你身上啥都没有
    }
```
**(10)对 eat cookie 命令的测试**
```java
    @Test
    public void testEat(){
        Command command = new Command(CommandWord.EAT, "魔法饼干");
        //给房间添加魔法饼干
        game.getPlayer().getCurrentRoom().addItem(new Item("魔法饼干","1",1));
        commandTableDriven.getTable().get(CommandWord.EAT).apply(command);
        //正确输入如下：
        //你吃了这个魔法饼干，感觉力大无穷，神清气爽，耐受+20kg你现在的容量为：70.0kg
    }

```