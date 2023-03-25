import {Component, OnInit} from '@angular/core';
import {NgForm, NgModel} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Observable, of} from 'rxjs';
import {Horse} from 'src/app/dto/horse';
import {Owner} from 'src/app/dto/owner';
import {Sex} from 'src/app/dto/sex';
import {HorseService} from 'src/app/service/horse.service';
import {OwnerService} from 'src/app/service/owner.service';
import {HttpClient} from '@angular/common/http';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs';
import {NgbModal,NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
export enum HorseCreateEditMode {
  create,
  edit,
  details,
};

@Component({
  selector: 'app-horse-create-edit',
  templateUrl: './horse-create-edit.component.html',
  styleUrls: ['./horse-create-edit.component.scss']
})
export class HorseCreateEditComponent implements OnInit {
  readonly rootUrl = 'localhost:8080';
  posts: any;
  mode: HorseCreateEditMode = HorseCreateEditMode.create;
  horse: Horse = {
    name: '',
    description: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
  };
  fatherId: any;
  motherId: any;
  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private http: HttpClient,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      case HorseCreateEditMode.edit:
        return 'Edit Horse';
      case HorseCreateEditMode.details:
        return 'Details of Horse';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      case HorseCreateEditMode.edit:
        return 'Save';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }
  get modeIsDetails(): boolean {
    return this.mode === HorseCreateEditMode.details;
  }
  get modeIsEdit(): boolean {
    return this.mode === HorseCreateEditMode.edit;
  }


  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      case HorseCreateEditMode.edit:
        return 'edited';
      default:
        return '?';
    }
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.searchByName(input, 5);

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });
    const num =Number(this.route.snapshot.paramMap.get('id'));
    switch (this.mode) {
      case HorseCreateEditMode.edit:
        this.loadData(num);
        break;
      case HorseCreateEditMode.details:
        this.loadData(num);
        break;
    }
  }

  public loadData(num: any){

    const observable = this.service.getById(num);
    observable.subscribe({
      next: data => {
        console.log('Horse given by id: ' + data.mother);
        this.horse.id = data.id;
        this.horse.name = data.name;
        this.horse.description = data.description;
        this.horse.sex = data.sex;
        this.horse.dateOfBirth = data.dateOfBirth;
        this.horse.owner = data.owner;
        this.horse.father = data.father;
        this.horse.mother = data.mother;
        if(this.horse.mother){
          console.log('Mother:' + this.horse.mother.id);
          this.motherId = this.horse.mother.id;
        }
        if(this.horse.father){
          console.log('Father:' + this.horse.father.id);
          this.fatherId = this.horse.father.id;
        }
        this.notification.success(`Horse ${this.horse.name} loaded successfully.`);
      },
      error: error => {
        console.error('Error loading horse', error);
        this.notification.error('Could not find a new horse with the given id. Errorcode: '
          + error.status + ', Errortext: ' + error.error.errors);
      }
    });
  }
  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }
  public switchToEdit(): void {
    console.log('Horseid:  ' + this.horse.id);
    this.router.navigate(['edit',this.horse.id]);
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.horse);
    if (form.valid) {
      if (this.horse.description === '') {
        delete this.horse.description;
      }
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          observable = this.service.create(this.horse);
          break;
        case HorseCreateEditMode.edit:
          const num =Number(this.route.snapshot.paramMap.get('id'));
          observable = this.service.update(num,this.horse);
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: error => {
          console.error('Error creating horse', error);
          this.notification.error('Could not create a new horse. Errorcode: ' + error.status + ', Errortext: ' + error.error.errors);
        }
      });
    }
  }
  public delete(){
    const num =Number(this.route.snapshot.paramMap.get('id'));
    const observable = this.service.delete(num);
    observable.subscribe({
      next: data => {
        this.notification.success(`Horse ${this.horse.name} successfully deleted.`);
        this.router.navigate(['/horses']);
      },
      error: error => {
        console.error('Error deleting horse', error);
        this.notification.error('Could not delete this horse. Errorcode: ' + error.status + ', Errortext: ' + error.error.errors);
      }
    });
  }
  formatter = (result: Horse) => result.name;
  searchMale: (text: Observable<string>) => Observable<Horse[]> = (text: Observable<string>) =>
    text.pipe(
      debounceTime(10),
      distinctUntilChanged(),
      switchMap((temp) => {
        if (temp.trim() === '') {
          this.horse.father = undefined;
          return of([]); // return an empty array if temp is an empty string
        }
        return this.service.filter(temp,Sex.male);
      })
    );
  searchFemale: (text: Observable<string>) => Observable<Horse[]> = (text: Observable<string>) =>
    text.pipe(
      debounceTime(10),
      distinctUntilChanged(),
      switchMap((temp) => this.service.filter(temp,Sex.female)));
}
