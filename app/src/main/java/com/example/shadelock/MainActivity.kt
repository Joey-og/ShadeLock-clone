package com.example.shadelock

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var dpm: DevicePolicyManager
    private lateinit var admin: ComponentName
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        admin = ComponentName(this, ShadeLockAdminReceiver::class.java)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 40, 32, 40)
        }
        val title = TextView(this).apply {
            text = "ShadeLock — قفل الستارة فقط"
            textSize = 23f
            gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            textSize = 17f
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 28)
        }
        val lockButton = Button(this).apply {
            text = "قفل الستارة"
            setOnClickListener { setShadeDisabled(true) }
        }
        val unlockButton = Button(this).apply {
            text = "فتح الستارة (طوارئ)"
            setOnClickListener { setShadeDisabled(false) }
        }
        val refreshButton = Button(this).apply {
            text = "تحديث الحالة"
            setOnClickListener { refreshStatus() }
        }
        val removeOwnerButton = Button(this).apply {
            text = "إلغاء إدارة الجهاز (استرجاع)"
            setOnClickListener {
                if (dpm.isDeviceOwnerApp(packageName)) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("إلغاء إدارة الجهاز")
                        .setMessage("سيزيل ShadeLock صلاحية Device Owner. لن يمسح بيانات الهاتف. متابعة؟")
                        .setNegativeButton("رجوع", null)
                        .setPositiveButton("إلغاء الإدارة") { _, _ ->
                            try {
                                dpm.clearDeviceOwnerApp(packageName)
                                status.text = "تم إلغاء إدارة الجهاز. أعد تحديث الحالة."
                            } catch (e: Exception) {
                                status.text = "تعذر الإلغاء: ${e.javaClass.simpleName}: ${e.message ?: "خطأ"}"
                            }
                        }
                        .show()
                } else {
                    status.text = "التطبيق ليس Device Owner حاليًا."
                }
            }
        }
        val instructions = TextView(this).apply {
            text = "التطبيق لا يغيّر الشاشة الرئيسية ولا يحذف ملفات ولا يقفل التطبيقات.\n\nيلزم تفعيل ShadeLock كـ Device Owner بواسطة ADB حتى يعمل قفل الستارة. لا تفعّل القفل قبل تجربة زر فتح الستارة."
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 0)
        }
        root.addView(title)
        root.addView(status)
        root.addView(lockButton)
        root.addView(unlockButton)
        root.addView(refreshButton)
        root.addView(removeOwnerButton)
        root.addView(instructions)
        setContentView(root)
        refreshStatus()
    }

    private fun setShadeDisabled(disabled: Boolean) {
        if (!dpm.isDeviceOwnerApp(packageName)) {
            status.text = "غير مفعّل كـ Device Owner. لا يمكن للتطبيق العادي قفل الستارة."
            return
        }
        try {
            val success = dpm.setStatusBarDisabled(admin, disabled)
            status.text = if (success) {
                if (disabled) "تم طلب قفل الستارة. اختبر السحب من أعلى الشاشة." else "تم طلب فتح الستارة."
            } else "النظام رفض تنفيذ الإعداد."
        } catch (e: Exception) {
            status.text = "تعذر تنفيذ الأمر: ${e.javaClass.simpleName}: ${e.message ?: "خطأ غير معروف"}"
        }
    }

    private fun refreshStatus() {
        status.text = if (dpm.isDeviceOwnerApp(packageName)) {
            "ShadeLock مسجّل كـ Device Owner.\nاستخدم زر فتح الستارة كخيار استرجاع."
        } else {
            "ShadeLock ليس Device Owner.\nالقفل لن يعمل قبل تفعيل المالك عبر ADB."
        }
    }
}
