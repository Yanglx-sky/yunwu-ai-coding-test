import java.util.List;

/**
 * 本次考核新增的测试：任务一（按书名搜索）与任务二（删除图书）。
 *
 * 命名遵循既有约定：类名以 Test 结尾；方法为无参 public void 且以 test 开头；
 * 使用 Assert 断言，TestRunner 自动发现，无需修改 runner。
 */
public class SearchAndDeleteTest {

    // ---------- 任务一：按书名搜索（contains 语义） ----------

    /** 部分关键字应能命中。 */
    public void testSearchMatchesPartialKeyword() {
        Library library = new Library(true);
        List<Book> byJava = library.search("Java");
        Assert.assertEquals(1, byJava.size(), "关键字 Java 应命中 1 本");
        Assert.assertEquals("Java入门", byJava.get(0).getName(), "命中的应是 Java入门");
        Assert.assertEquals(1, library.search("入门").size(), "关键字 入门 应命中 1 本");
        Assert.assertEquals(1, library.search("数据结构").size(), "全名仍应命中 1 本");
    }

    /** 只匹配书名，不匹配作者。 */
    public void testSearchDoesNotMatchAuthor() {
        Library library = new Library(true);
        Assert.assertEquals(0, library.search("张").size(), "作者名不应被匹配");
        Assert.assertEquals(0, library.search("李四").size(), "作者全名不应被匹配");
        Assert.assertEquals(0, library.search("王五").size(), "作者全名不应被匹配");
    }

    /** 区分大小写。 */
    public void testSearchIsCaseSensitive() {
        Library library = new Library(true);
        Assert.assertEquals(0, library.search("java").size(), "小写 java 不应命中");
        Assert.assertEquals(0, library.search("JAVA").size(), "全大写 JAVA 不应命中");
        Assert.assertEquals(1, library.search("Java").size(), "原始大小写应命中");
    }

    /** 无命中返回空列表；空关键字不得抛异常。 */
    public void testSearchNoMatchAndEmptyKeyword() {
        Library library = new Library(true);
        List<Book> none = library.search("不存在的书名");
        Assert.assertNotNull(none, "无命中应返回空列表而不是 null");
        Assert.assertEquals(0, none.size(), "无命中应为空列表");
        List<Book> all = library.search("");
        Assert.assertNotNull(all, "空关键字不得抛出异常，也不应返回 null");
    }

    // ---------- 任务二：删除图书 ----------

    /** 删除成功：从书库移除，剩余图书的编号、顺序、状态都不变。 */
    public void testDeleteSuccessKeepsRemainingBooks() {
        Library library = new Library(true);
        Assert.assertEquals("删除成功", library.delete(2), "删除已存在的编号应成功");
        List<Book> books = library.listAll();
        Assert.assertEquals(2, books.size(), "删除后应剩 2 本");
        Assert.assertEquals(1, books.get(0).getId(), "剩余第一本编号应为 1");
        Assert.assertEquals(3, books.get(1).getId(), "剩余第二本编号应为 3");
        Assert.assertFalse(books.get(0).isBorrowed(), "剩余图书状态不应变化");
        Assert.assertFalse(books.get(1).isBorrowed(), "剩余图书状态不应变化");
    }

    /** 删除后全链路不可见：查找、搜索、借阅、归还都感知不到。 */
    public void testDeletedBookIsInvisibleEverywhere() {
        Library library = new Library(true);
        library.delete(2);
        Assert.assertNull(library.findById(2), "删除后 findById 应返回 null");
        Assert.assertEquals(0, library.search("数据结构").size(), "删除后不应再被搜到");
        Assert.assertEquals("图书编号不存在", library.borrow(2), "删除后借阅应提示编号不存在");
        Assert.assertEquals("图书编号不存在", library.returnBook(2), "删除后归还应提示编号不存在");
    }

    /** 编号不存在：越界编号、0、负数与重复删除。 */
    public void testDeleteUnknownId() {
        Library library = new Library(true);
        Assert.assertEquals("图书编号不存在", library.delete(999), "越界编号应提示编号不存在");
        Assert.assertEquals("图书编号不存在", library.delete(0), "编号 0 应提示编号不存在");
        Assert.assertEquals("图书编号不存在", library.delete(-1), "负编号应提示编号不存在");
        Assert.assertEquals("删除成功", library.delete(2), "首次删除编号 2 应成功");
        Assert.assertEquals("图书编号不存在", library.delete(2), "重复删除应提示编号不存在");
    }

    /** 编号不复用：删除后添加新书，编号继续自增。 */
    public void testDeleteDoesNotReuseId() {
        Library library = new Library(true);
        Assert.assertEquals("删除成功", library.delete(2), "删除编号 2");
        Book fourth = library.add("新书", "测试", 30.0);
        Assert.assertEquals(4, fourth.getId(), "删除 2 后再添加应得编号 4，而不是复用 2");
        Assert.assertEquals(3, library.listAll().size(), "删 1 本再加 1 本，数量应回到 3");
    }

    /** 删空后再添加，编号在历史已分配基础上继续递增，不回到 1。 */
    public void testAddAfterDeletingAllContinuesIdSequence() {
        Library library = new Library(true);
        Assert.assertEquals("删除成功", library.delete(1), "删除编号 1");
        Assert.assertEquals("删除成功", library.delete(2), "删除编号 2");
        Assert.assertEquals("删除成功", library.delete(3), "删除编号 3");
        Assert.assertEquals(0, library.listAll().size(), "删除全部后书库应为空");

        Book fourth = library.add("新书", "测试", 30.0);
        Assert.assertEquals(4, fourth.getId(), "删空后首次添加应得编号 4，不回到 1");

        Assert.assertEquals("删除成功", library.delete(4), "删除编号 4");
        Assert.assertEquals(0, library.listAll().size(), "再次删空书库");
        Book fifth = library.add("另一本", "测试", 40.0);
        Assert.assertEquals(5, fifth.getId(), "已分配过编号 4 之后应得编号 5");
    }
}
