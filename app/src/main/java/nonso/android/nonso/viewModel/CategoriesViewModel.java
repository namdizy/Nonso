package nonso.android.nonso.viewModel;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.interfaces.CategoriesCallback;

public class CategoriesViewModel extends ViewModel {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String DATABASE_COLLECTION_CATEGORIES = "categories";
    private static final String DATABASE_DOCUMENT_CATEGORIES = "categories";

    DocumentReference mCategoriesRef;
    private Map<String, Object> mCategories;
    private String[] mCategoriesList;


    public CategoriesViewModel(){
        mCategoriesRef  = db.collection(DATABASE_COLLECTION_CATEGORIES).document(DATABASE_DOCUMENT_CATEGORIES);
    }


    public void getCategories(CategoriesCallback categoriesCallback){
        mCategoriesRef.get().addOnSuccessListener(documentSnapshot ->  {
                mCategories =  documentSnapshot.getData();
                mCategoriesList = mCategories.keySet().toArray(new String[mCategories.keySet().size()]);
                categoriesCallback.categories(mCategoriesList);
        }).addOnFailureListener(e -> categoriesCallback.result(Result.FAILED));

    }
}
