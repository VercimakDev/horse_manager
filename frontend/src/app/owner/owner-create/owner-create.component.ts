import { Component } from '@angular/core';
import {NgForm, NgModel} from '@angular/forms';
import {Observable} from 'rxjs';
import {Horse} from '../../dto/horse';
import {HorseCreateEditMode} from '../../component/horse/horse-create-edit/horse-create-edit.component';
import {Owner} from '../../dto/owner';
import {HorseService} from "../../service/horse.service";
import {OwnerService} from "../../service/owner.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-owner-create',
  templateUrl: './owner-create.component.html',
  styleUrls: ['./owner-create.component.scss']
})
export class OwnerCreateComponent {
  owner: Owner = {
    firstName: '',
    lastName: '',
    email: '',
  };
  constructor(
    private service: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {}
  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': !input.valid && !input.pristine,
    };
  }
  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.owner);
    if (form.valid) {
      if (this.owner.email === '') {
        delete this.owner.email;
      }
      const observable = this.service.create(this.owner);
      observable.subscribe({
        next: data => {
          this.notification.success(`Owner ${this.owner.firstName}  ${this.owner.lastName} created succesfully`);
          this.router.navigate(['/owners']);
        },
        error: error => {
          console.error('Error creating owner', error);
          this.notification.error('Could not create a new owner. Errorcode: ' + error.status + ', Errortext: ' + error.error.errors);
        }
      });
      }
    }
}
