package com.amirami.simapp.radiostations.ui

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import com.amirami.simapp.radiostations.MainActivity.Companion.BASE_URL
import com.amirami.simapp.radiostations.MainActivity.Companion.saveData
import com.amirami.simapp.radiostations.MainActivity.Companion.userRecord
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.countryCodeToName
import com.amirami.simapp.radiostations.RadioFunction.errorToast
import com.amirami.simapp.radiostations.RadioFunction.getCurrentDate
import com.amirami.simapp.radiostations.RadioFunction.getuserid
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.RadioFunction.succesToast
import com.amirami.simapp.radiostations.data.datastore.viewmodel.DataViewModel
import com.amirami.simapp.radiostations.databinding.FragmentSettingBinding
import com.amirami.simapp.radiostations.model.FavoriteFirestore
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.utils.exhaustive
import com.amirami.simapp.radiostations.viewmodel.FavoriteFirestoreViewModel
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@UnstableApi @AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {

    private lateinit var binding: FragmentSettingBinding

    private val infoViewModel: InfoViewModel by activityViewModels()
    private val dataViewModel: DataViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private val favoriteFirestoreViewModel: FavoriteFirestoreViewModel by activityViewModels()


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode == RESULT_OK) {
            // Successfully signed in

            val user = FirebaseAuth.getInstance().currentUser
            binding.signinOutItxVw.text = resources.getString(R.string.Déconnecter)
            // _binding?.signinOutImVw?.setImageResource(R.drawable.ic_signout)
            binding.signinOutItxVw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_signout, 0, 0, 0)

            succesToast(requireContext(), user?.displayName + " " + resources.getString(R.string.connectionsuccess))
            // ...
            getRadioUidListFromFirestoreAndITSaveInRoom(true)

            userTxtVwVisibiity(true)
        }
        else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            userTxtVwVisibiity(false)

            errorToast(requireContext(), resources.getString(R.string.Échec_connexion))
        }
    }
    private fun userTxtVwVisibiity(visible: Boolean) {
        if (visible) {
            binding.syncFavTxVw.visibility = View.VISIBLE
            binding.deleteAccountTxVw.visibility = View.VISIBLE
        } else {
            binding.syncFavTxVw.visibility = View.GONE
            binding.deleteAccountTxVw.visibility = View.GONE
        }
    }

    private fun createUserDocument(favoriteFirestore: FavoriteFirestore) {
        val isProductAddLiveData = favoriteFirestoreViewModel.addUserDocumentInFirestore(favoriteFirestore)

        isProductAddLiveData.observe(viewLifecycleOwner) { dataOrException ->
            val isProductAdded = dataOrException.data
            if (isProductAdded != null) {
                hideProgressBar()
                /*  if (isProductAdded) {  }*/
            }
             if (dataOrException.e != null) {
                 errorToast(requireContext(),dataOrException.e!!)
              /*   if(dataOrException.e=="getRadioUID"){

                 }*/

             }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingBinding.bind(view)
        RadioFunction.interatialadsLoad(requireContext())
        infoViewModel.putTitleText(getString(R.string.Settings))

        conectDisconnectBtn()
    /*    if(getuserid()!="no_user"){
            userTxtVwVisibiity(true)
        }*/

                binding.favCountryTxv.text = getString(R.string.defaultCountry, countryCodeToName(dataViewModel.getDefaultCountr()))


        binding.radioServersTxv.text = getString(R.string.currentserver, BASE_URL)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.dialogueEvents.collectLatest { event ->
                    when (event) {
                        is InfoViewModel.ChooseDefBottomSheetEvents.PutDefCountryInfo -> {
                            run {
                                binding.favCountryTxv.text = getString(R.string.defaultCountry, event.country)
                            }
                        }

                        is InfoViewModel.ChooseDefBottomSheetEvents.PutDefServerInfo -> {
                            run {
                                binding.radioServersTxv.text = getString(R.string.currentserver, event.server)
                            }
                        }
                        is InfoViewModel.ChooseDefBottomSheetEvents.PutDefThemeInfo -> {
                            run {
                            }
                        }

                        is InfoViewModel.ChooseDefBottomSheetEvents.PutLogInDialogueInfo -> {
                            run {
                                if (event.id == "signinOut") signOut()
                            }
                        }
                        is InfoViewModel.ChooseDefBottomSheetEvents.PutDeleteUsersDialogueInfo -> {
                            run {
                                if (event.id == "deleteUser") {
                                    val user = userRecord.currentUser!!
                                    user.delete().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            succesToast(requireContext(), getString(R.string.sucssessDeleteUser))
                                            conectDisconnectBtn()
                                        } else errorToast(requireContext(), task.exception!!.message!!)
                                    }
                                }
                            }
                        }
                    }.exhaustive
                }
            }
        }

        binding.saveData.isChecked = saveData

        binding.saveData.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.saveData.text = getString(R.string.Disable_Load_radio_images)
                dataViewModel.saveSaveData(true)
                saveData = true
            } else {
                binding.saveData.text = getString(R.string.Enable_Load_radio_images)
                dataViewModel.saveSaveData(false)
                saveData = false
            }
        }

        binding.deleteAccountTxVw.setSafeOnClickListener {
            goToDeleteUserDialog()
        }

        binding.syncFavTxVw.setSafeOnClickListener {
            infoViewModel.deleteAll("")
            getRadioUidListFromFirestoreAndITSaveInRoom(false)
        }

        binding.StaticsTxV.setSafeOnClickListener {
            retrofitRadioViewModel.getStatis()
            this@SettingFragment.findNavController().navigate(R.id.action_fragmentSetting_to_statisticBottomSheetFragment) //       NavHostFragment.findNavController(this).navigate(R.id.action_fragmentSetting_to_statisticBottomSheetFragment)
        }

        binding.LicencesTxtV.setSafeOnClickListener {
          //  this@SettingFragment.findNavController().navigate(R.id.action_fragmentSetting_to_licencesBottomSheetFragment) //       NavHostFragment.findNavController(this).navigate(R.id.action_fragmentSetting_to_licencesBottomSheetFragment)

            startActivity(Intent(requireActivity(), OssLicensesMenuActivity::class.java))


        }

        binding.ContactUsTxV.setSafeOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "amirami.simapp@gmail.com", null))
            startActivity(Intent.createChooser(intent, getString(R.string.Choose_Emailclient)))
        }

        binding.DisclaimerTxtV.setSafeOnClickListener {
            val action = SettingFragmentDirections.actionFragmentSettingToInfoBottomSheetFragment(getString(R.string.discaimertitle), getString(R.string.disclaimermessage))
            this@SettingFragment.findNavController().navigate(action) //       NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }

        binding.batterisettings.setSafeOnClickListener {
            val action = SettingFragmentDirections.actionFragmentSettingToInfoBottomSheetFragment("BatterieOptimisation")
            this@SettingFragment.findNavController().navigate(action) //     NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }

        binding.themeTxvw.setSafeOnClickListener {
            val action = SettingFragmentDirections.actionFragmentSettingToChooseDefBottomSheetFragment("deftheme")
            this@SettingFragment.findNavController().navigate(action) //        NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }

        binding.favCountryTxv.setSafeOnClickListener {
            val action = SettingFragmentDirections.actionFragmentSettingToChooseDefBottomSheetFragment("defcountry")
            this@SettingFragment.findNavController().navigate(action) //      NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }

        binding.radioServersTxv.setSafeOnClickListener {
            val action = SettingFragmentDirections.actionFragmentSettingToChooseDefBottomSheetFragment("defserver")
            this@SettingFragment.findNavController().navigate(action) //      NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }

        binding.moreappsTxvw.setSafeOnClickListener {
            moreApps()
        }

        binding.rateTxvw.setSafeOnClickListener {
            rate()
        }

        binding.removeadsTxvw.setSafeOnClickListener {
            removeAds()
        }
    }

    private fun checkEmailExistsOrNot() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(FirebaseAuth.getInstance().currentUser!!.email!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result?.signInMethods!!.size == 0) setuserNotexistui() // email not existed
                        else setuserexistui() // email existed
                    }
                    //  else errorToast(requireContext(),task.exception!!.message.toString())// THIS LINE WORK BUT IT REDUNDENT
                }
                .addOnFailureListener { e -> errorToast(requireContext(), e.message.toString()) }
        } else setuserNotexistui()
    }

    private fun setuserexistui() {
        userTxtVwVisibiity(true)
        binding.signinOutItxVw.apply {
            text = resources.getString(R.string.Déconnecter)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_signout, 0, 0, 0)
        }
    }
    private fun setuserNotexistui() {
        userTxtVwVisibiity(false)
        binding.signinOutItxVw.apply {
            text = resources.getString(R.string.Connecter)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_signin, 0, 0, 0)
        }
    }
    private fun conectDisconnectBtn() {
        checkEmailExistsOrNot()
        // getuserid()!= "no_user"
        binding.signinOutItxVw.setSafeOnClickListener {
            if (binding.signinOutItxVw.text.toString() == resources.getString(R.string.Déconnecter)) {
                goToLogOutDialog()
            } else createSignInIntent()
        }
    }



    private fun moreApps() {
        val uri = Uri.parse("https://play.google.com/store/apps/developer?id=AmiRami")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            (
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=AmiRami")))
        }
    }

    private fun removeAds() {
        val uri = Uri.parse("http://play.google.com/store/apps/details?id=com.amirami.simapp.radiobroadcastpro")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            (
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.amirami.simapp.radiobroadcastpro")))
        }
    }

    private fun rate() {
        var uri = Uri.parse("market://details?id=" + requireContext().packageName)
        var goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            (
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            uri = Uri.parse("https://play.google.com/store/apps/details?id=" + requireContext().packageName)
            goToMarket = Intent(Intent.ACTION_VIEW, uri)
            startActivity(/*   Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName))*/
                goToMarket
            )
        }
    }

    private fun goToLogOutDialog() {
        val action = SettingFragmentDirections.actionFragmentSettingToInfoBottomSheetFragment("signinOut")
        this@SettingFragment.findNavController().navigate(action) //     NavHostFragment.findNavController(requireParentFragment()).navigate(action)
    }

    private fun goToDeleteUserDialog() {
        val action = SettingFragmentDirections.actionFragmentSettingToInfoBottomSheetFragment("deleteUser")
        this@SettingFragment.findNavController().navigate(action) //     NavHostFragment.findNavController(requireParentFragment()).navigate(action)
    }

    private fun createSignInIntent() {
        // when change theme !!! to check
        // val providers = emptyList<AuthUI.IdpConfig>()
        // [START auth_fui_create_intent]
        // Choose authentication providers

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
            // AuthUI.IdpConfig.PhoneBuilder().build(),
            //   AuthUI.IdpConfig.GoogleBuilder().build()
            // AuthUI.IdpConfig.FacebookBuilder().build(),
            // AuthUI.IdpConfig.TwitterBuilder().build()
        )
        // Create and launch sign-in intent

        /*startForResult.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                //   .setLogo(R.drawable.my_great_logo) // Set logo drawable
                 .setTheme(R.style.FirebaseUI) // Set theme
                .build()
        )*/



        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .enableAnonymousUsersAutoUpgrade()
            //  .setAuthMethodPickerLayout(customLayout)
            .setLogo(R.drawable.ic_radio_svg) // Set logo drawable
            .setTheme(R.style.FirebaseUI) // Set theme
            .build()

        signInLauncher.launch(signInIntent)
        // [END auth_fui_create_intent]
    }

    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
                // ...
                binding.signinOutItxVw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_signin, 0, 0, 0)
                // _binding?.signinOutImVw?.setImageResource(R.drawable.ic_signin)
                binding.signinOutItxVw.text = resources.getString(R.string.Connecter)
                errorToast(requireContext(), resources.getString(R.string.Déconnectersuccess))

                userTxtVwVisibiity(false)
            }
            .addOnCanceledListener {
                binding.signinOutItxVw.text = resources.getString(R.string.Déconnecter)
                // _binding?.signinOutImVw?.setImageResource(R.drawable.ic_signout)
                binding.signinOutItxVw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_signout, 0, 0, 0)

                userTxtVwVisibiity(true)
            }
        // [END auth_fui_signout]
    }

    private fun getRadioUidListFromFirestoreAndITSaveInRoom(saveRoomToFirestore: Boolean) {
        displayProgressBar()
        favoriteFirestoreViewModel.getAllRadioFavoriteListFromFirestore.observe(viewLifecycleOwner) { DataOrExceptionProdNames ->
            val productList = DataOrExceptionProdNames.data
            if (productList != null && productList.isNotEmpty()) {
                saveFaveRadioFromFirestoreToRoom()
                for (i in 0 until productList[0].size) {
                    retrofitRadioViewModel.getRadiosByUId(productList[0][i])
                    if (i == productList[0].size - 1 && saveRoomToFirestore) getFavRadioRoom(false)
                }
                hideProgressBar()
            }

            if (DataOrExceptionProdNames.e != null) {
                if (saveRoomToFirestore) getFavRadioRoom(true)
                hideProgressBar()
                //  errorToast( requireContext(),DataOrExceptionProdNames.e.toString())
            }
        }
    }

    private fun saveFaveRadioFromFirestoreToRoom() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                retrofitRadioViewModel.responseRadioUID.collectLatest { response ->
                    when (response.status) {
                        Status.SUCCESS -> {
                            if (response.data != null) {
                                hideProgressBar()
                                val radio = response.data[0]

                                radio.fav = true
                                infoViewModel.upsertRadio(radio, "Radio added")
                            }
                            // else showErrorConnection(response.message!!)
                        }
                        Status.ERROR -> {
                            hideProgressBar()
                            // showErrorConnection(response.message!!)
                        }
                        Status.LOADING -> {
                            displayProgressBar()
                        }
                    }
                }
            }
        }
    }

    private fun displayProgressBar() {
       binding.spinKitSetting.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.spinKitSetting.visibility = View.GONE
        RadioFunction.interatialadsShow(requireContext())
    }

    private fun getFavRadioRoom(createDocument : Boolean) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.favList.collectLatest { list ->
                    //    Log.d("MainFragment","ID ${list.map { it.id }}, Name ${list.map { it.name }}")
                    if (list.isNotEmpty()) {
                        val favoriteFirestore = FavoriteFirestore(
                            user_id = getuserid(),
                            radio_favorites_list=  list.map { it.stationuuid } as ArrayList<String>,
                            last_date_modified =  getCurrentDate()
                        )
                     if(createDocument)   createUserDocument(favoriteFirestore)
                    } else  if(createDocument)  createUserDocument(FavoriteFirestore(getuserid(), ArrayList<String>(), getCurrentDate()))
                }
            }
        }


    }


}
