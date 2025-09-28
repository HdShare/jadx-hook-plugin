package me.hd.jadx.plugins.ui

import jadx.api.JadxDecompiler
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.*

class DexKitSearchDialog(mainFrame: JFrame, decompiler: JadxDecompiler) : JDialog(mainFrame) {
	private val searchField = JTextField(30)
	private val searchButton = JButton("搜索")
	private val resultsArea = JTextArea(20, 50)

	init {
		initUI()
	}

	private fun initUI() {
		title = "DexKit Search"
		defaultCloseOperation = DISPOSE_ON_CLOSE
		setLocationRelativeTo(owner)
		contentPane = JPanel(BorderLayout(10, 10)).apply {
			border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
			add(JPanel(BorderLayout(10, 10)).apply {
				add(JLabel("搜索内容:"), BorderLayout.WEST)
				add(JPanel(BorderLayout(5, 5)).apply {
					add(searchField, BorderLayout.CENTER)
					add(searchButton.apply {
						addActionListener { onSearch() }
					}, BorderLayout.EAST)
				}, BorderLayout.CENTER)
			}, BorderLayout.NORTH)
			add(JPanel(BorderLayout()).apply {
				add(JLabel("搜索结果:"), BorderLayout.NORTH)
				add(JScrollPane(resultsArea.apply {
					font = Font(Font.MONOSPACED, Font.PLAIN, 12)
					isEditable = false
				}).apply {
					verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
					horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
				}, BorderLayout.CENTER)
			}, BorderLayout.CENTER)
		}
		pack()
	}

	private fun onSearch() {
		val keyword = searchField.text.trim()
		if (keyword.isEmpty()) {
			JOptionPane.showMessageDialog(this, "请输入搜索内容", "提示", JOptionPane.WARNING_MESSAGE)
			searchField.requestFocus()
			return
		}
		performSearch(keyword)
	}

	private fun performSearch(keyword: String) {
		searchButton.isEnabled = false
		resultsArea.text = "搜索中...\n\n"
		object : SwingWorker<String, Void>() {
			override fun doInBackground(): String {
				return try {
					val results = doSearch(keyword)
					if (results.isEmpty()) {
						"未找到: $keyword"
					} else {
						"找到 ${results.size} 个结果:\n\n${results.joinToString("\n")}"
					}
				} catch (e: Exception) {
					"搜索出错: ${e.message}"
				}
			}

			override fun done() {
				try {
					resultsArea.text = get()
				} catch (e: Exception) {
					resultsArea.text = "搜索失败: ${e.message}"
				} finally {
					searchButton.isEnabled = true
					searchField.requestFocus()
				}
			}
		}.execute()
	}

	private fun doSearch(keyword: String): List<String> {
		val results = mutableListOf<String>()
		for (i in 0..50) {
			results.add("$i: 测试 $keyword")
		}
		return results
	}

	private fun showDialog() {
		isVisible = true
		searchField.requestFocus()
	}

	companion object {
		fun showDialog(mainFrame: JFrame, decompiler: JadxDecompiler) {
			val dialog = DexKitSearchDialog(mainFrame, decompiler)
			dialog.showDialog()
		}
	}
}
