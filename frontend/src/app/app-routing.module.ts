import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HorseCreateEditComponent, HorseCreateEditMode} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import {OwnerComponent} from './owner/owner.component';
import {OwnerCreateComponent} from './owner/owner-create/owner-create.component';
import {FamilyTreeComponent} from './component/horse/family-tree/family-tree.component';

const routes: Routes = [
  {path: '', redirectTo: 'horses', pathMatch: 'full'},
  {path: 'horses', children: [
    {path: '', component: HorseComponent},
      {path: 'tree/:id', component: FamilyTreeComponent},
    {path: 'create', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.create}},
      {path: 'edit/:id', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.edit},},
      {path: 'details/:id', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.details},},
  ]},
  {path: 'owners', children: [
      {path: '', component: OwnerComponent},
      {path: 'create', component: OwnerCreateComponent},
    ]},
  {path: '**', redirectTo: 'horses'},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
