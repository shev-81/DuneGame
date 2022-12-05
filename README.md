<h1 align="center">Игра Dune.
<h3 align="center">LibGDX Framework, Java SE + FX.</h3>

 ---------------------

Dune (стратегия) - десктопное приложение, которое можно портировать на Android ОС и запускать на мобильных устройствах.
В игре 2 стороны конфликта, зеленые (Игрок) и красные (NPC), на старте каждый получает спавн ресурсов (Spice) и танк (Harvester) для его сбора.

> <b>Техническая часть</b>
 - `IDE: IntelliJ IDEA 2021.3.3`
 - `Версия JDK: 1.8.0_121.`
 - `LibGDX Framework`
 
> <b>Используемые технологии:</b>
 - `Stream API`
 - `CSS`
 - `Gradle`
 
 > <b>Вид программы в Production.</b>

![Снимок экрана игра](https://user-images.githubusercontent.com/89448563/197381192-897dba5a-a0a9-4ab2-ac6e-7d0005fac92a.png)

Развивая свои войска можно:  Улучшать свою базу (повышать колличество доступных юнитов), улучшать оружие у юнитов. 
- в игре доступны несколько типов юнитов: Harvester (отвечает за сбор ресурсов), Batle tank (боевой танк на старте имеет пушку при улучшении получает ракетное вооружение),
  Healer (лекарь - лечит поврежденные юниты и базу).
- Для CPU создан командный ИИ, который упарвляет развитием базы NPC постройкой юнитов и организацией атак на пользователя.
- Каждый юнит в части касающейся также имеет свой ИИ. К примеру если назначить точку сбора ресурсов для Harvester'a он наполнив свой резервуар поедет сдавать ресурсы на базу 
  и затем сгрузив их вернется на точку спавна ресурсов, а если их там не будет найдет на карте ближайший спавн и отправится для сбора туда. 
  Боевые танки имеют радиус агро на врага, если приближается на определенную дистанцию начинается бой. Лекари находясь рядом со своими войсками при потере ими 25% HP 
  автоматически начинают лечить союзных юнитов, без вмешательства пользователя.
- Игра заканчивается после полной победы любой из сторон (повержена База, и отсутствуют все юниты).

В игре активно используется паттерн Object Pool, так как объектов взаимодействия очень много (все юниты, здания, снаряды и ракеты, spice, импульсы от Healers).
Такое использование позволяет сильно снизить потребление памяти и использовать неактивные объекты повторно.

    public abstract class ObjectPool  <T extends Poolable> {
    protected List<T> activeList;
    protected List<T> freeList;
    protected GameController gameController;
    protected int initialCapacity;
    
    protected abstract T newObject();

    public ObjectPool(GameController gameController) {
        this.initialCapacity = 10;  // начальный размер пула объектов
        this.gameController = gameController;
        this.activeList = new ArrayList<>(initialCapacity);
        this.freeList = new ArrayList<>(initialCapacity);
    }

    public T getActiveElement() {
        if(freeList.size()==0){
            freeList.add(newObject());
        }
        T tempObject = freeList.remove(freeList.size()-1);
        activeList.add(tempObject);
        return tempObject;
    }
    
Для того что бы создать новый игровой объект необходимо его класс унаследовать от ObjectPool и создать этот объект методом `T getActiveElement()` - полученный объект будет помещен в список пула. При изменении статуса объекта на неактивный он будет перенесен в список неактивных объектов (их можно переиспользовать повторно). 

Большое колличество работы связанно с графическими объектами (OpenGL). Рисунки подготавливались с использованием Aseprite (Программа создания анимированных изображений), 
а затем упаковывались в карту изображений. Карта использовалась для предоставления регионов изображений для отрисовки объектов в игре. 

Все расчеты взаимодействия объектов между собой основаны на использовании векторной математики. Как то скорость полета снарядов и ракет и юнитов, их направление.
Растояние при приближении снарядов к вражеским юнитам (попадание по достижении врага) при котором происходит попадание и взрыв так же расчитывается 
на основании векторного взаимодействия.   

Проект создан под управлением Gradle.
