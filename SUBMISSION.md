# SUBMISSION

任务一：`search` 由 `equals` 改为 `contains` 包含匹配，只比书名、区分大小写，不涉作者。

任务二：`Library` 新增 `delete(id)`：编号不存在返回"图书编号不存在"，否则移除并返回"删除成功"；编号只增不减、删除不回退（删 2 后新增得 4）；`Main` 菜单插入删除项，退出顺延为 6。

任务三：新增 `SearchAndDeleteTest` 共 9 个用例，覆盖搜索四情形与删除五类断言；`LibraryTest` 未改动。
